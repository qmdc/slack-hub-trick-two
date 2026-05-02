package com.slack.slackjarservice.taskdashboard.service.impl.spacedrepetition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.taskdashboard.dao.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition.*;
import com.slack.slackjarservice.taskdashboard.service.spacedrepetition.StudyStatService;
import com.slack.slackjarservice.taskdashboard.util.SpacedRepetitionAlgorithm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class StudyStatServiceImpl extends ServiceImpl<StudyStatDao, StudyStat> implements StudyStatService {

    @Resource
    private DeckDao deckDao;

    @Resource
    private CardDao cardDao;

    @Resource
    private ReviewRecordDao reviewRecordDao;

    @Resource
    private TagDao tagDao;

    @Resource
    private CardTagDao cardTagDao;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public StudyDashboardDTO getStudyDashboard(Long userId) {
        StudyDashboardDTO dto = new StudyDashboardDTO();

        String today = LocalDate.now().format(DATE_FORMATTER);
        StudyStat todayStat = getOrCreateTodayStat(userId, today);

        LambdaQueryWrapper<Deck> deckWrapper = new LambdaQueryWrapper<>();
        deckWrapper.eq(Deck::getUserId, userId);
        long deckCount = deckDao.selectCount(deckWrapper);
        dto.setTotalDecks((int) deckCount);

        LambdaQueryWrapper<Card> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(Card::getUserId, userId);
        long cardCount = cardDao.selectCount(cardWrapper);
        dto.setTotalCards((int) cardCount);

        LambdaQueryWrapper<Card> reviewedWrapper = new LambdaQueryWrapper<>();
        reviewedWrapper.eq(Card::getUserId, userId)
                .gt(Card::getReviewCount, 0);
        long reviewedCardCount = cardDao.selectCount(reviewedWrapper);
        dto.setTotalReviewedCards((int) reviewedCardCount);

        LambdaQueryWrapper<Card> masteredWrapper = new LambdaQueryWrapper<>();
        masteredWrapper.eq(Card::getUserId, userId)
                .ge(Card::getMasteryLevel, 3);
        long masteredCardCount = cardDao.selectCount(masteredWrapper);
        dto.setTotalMasteredCards((int) masteredCardCount);

        if (cardCount > 0) {
            BigDecimal masteryRate = new BigDecimal(masteredCardCount)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(cardCount), 2, RoundingMode.HALF_UP);
            dto.setOverallMasteryRate(masteryRate);
        } else {
            dto.setOverallMasteryRate(BigDecimal.ZERO);
        }

        long todayPendingReview = countTodayReviewCards(userId);
        dto.setTodayPendingReview((int) todayPendingReview);

        dto.setTodayReviewed(todayStat.getReviewedCards() != null ? todayStat.getReviewedCards() : 0);
        dto.setTodayNewCards(todayStat.getNewCards() != null ? todayStat.getNewCards() : 0);
        dto.setTodayCorrectCount(todayStat.getCorrectCount() != null ? todayStat.getCorrectCount() : 0);
        dto.setTodayIncorrectCount(todayStat.getIncorrectCount() != null ? todayStat.getIncorrectCount() : 0);
        dto.setTodayAccuracyRate(todayStat.getAccuracyRate() != null ? todayStat.getAccuracyRate() : BigDecimal.ZERO);
        dto.setTodayStudyDuration(todayStat.getStudyDuration() != null ? todayStat.getStudyDuration() : 0L);
        dto.setTodayStudyDurationFormatted(formatDuration(dto.getTodayStudyDuration()));

        dto.setRecentDecks(getRecentDecks(userId));
        dto.setTodayReviewCards(getTodayReviewCardsPreview(userId));
        dto.setHotTags(getHotTags(userId));

        return dto;
    }

    @Override
    public StudyStatDTO getTodayStat(Long userId) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        StudyStat stat = getOrCreateTodayStat(userId, today);
        return convertToDTO(stat);
    }

    @Override
    public List<StudyStatDTO> getRecentStats(Long userId, Integer days) {
        if (days == null || days <= 0) {
            days = 7;
        }

        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        String startDateStr = startDate.format(DATE_FORMATTER);

        LambdaQueryWrapper<StudyStat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudyStat::getUserId, userId)
                .ge(StudyStat::getStatDate, startDateStr)
                .orderByAsc(StudyStat::getStatDate);

        List<StudyStat> stats = this.list(queryWrapper);
        return convertToDTOList(stats);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordStudyDuration(Long userId, Long duration) {
        if (duration == null || duration <= 0) {
            return;
        }

        String today = LocalDate.now().format(DATE_FORMATTER);
        StudyStat stat = getOrCreateTodayStat(userId, today);

        long newDuration = (stat.getStudyDuration() != null ? stat.getStudyDuration() : 0L) + duration;
        stat.setStudyDuration(newDuration);

        long totalDuration = (stat.getTotalStudyDuration() != null ? stat.getTotalStudyDuration() : 0L) + duration;
        stat.setTotalStudyDuration(totalDuration);

        this.updateById(stat);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordReviewResult(Long userId, boolean isCorrect, boolean isNewCard) {
        String today = LocalDate.now().format(DATE_FORMATTER);
        StudyStat stat = getOrCreateTodayStat(userId, today);

        int reviewedCards = (stat.getReviewedCards() != null ? stat.getReviewedCards() : 0) + 1;
        stat.setReviewedCards(reviewedCards);

        int totalReviewedCards = (stat.getTotalReviewedCards() != null ? stat.getTotalReviewedCards() : 0) + 1;
        stat.setTotalReviewedCards(totalReviewedCards);

        if (isNewCard) {
            int newCards = (stat.getNewCards() != null ? stat.getNewCards() : 0) + 1;
            stat.setNewCards(newCards);

            int totalLearnedCards = (stat.getTotalLearnedCards() != null ? stat.getTotalLearnedCards() : 0) + 1;
            stat.setTotalLearnedCards(totalLearnedCards);
        }

        if (isCorrect) {
            int correctCount = (stat.getCorrectCount() != null ? stat.getCorrectCount() : 0) + 1;
            stat.setCorrectCount(correctCount);
        } else {
            int incorrectCount = (stat.getIncorrectCount() != null ? stat.getIncorrectCount() : 0) + 1;
            stat.setIncorrectCount(incorrectCount);
        }

        int totalReviewed = stat.getReviewedCards();
        if (totalReviewed > 0) {
            int correct = stat.getCorrectCount() != null ? stat.getCorrectCount() : 0;
            BigDecimal accuracyRate = new BigDecimal(correct)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalReviewed), 2, RoundingMode.HALF_UP);
            stat.setAccuracyRate(accuracyRate);
        }

        stat.setTodayPendingReview((int) countTodayReviewCards(userId));

        this.updateById(stat);
    }

    @Override
    public StudyStatDTO convertToDTO(StudyStat stat) {
        if (stat == null) {
            return null;
        }

        StudyStatDTO dto = new StudyStatDTO();
        BeanUtils.copyProperties(stat, dto);

        if (stat.getStudyDuration() != null) {
            dto.setStudyDurationFormatted(formatDuration(stat.getStudyDuration()));
        }
        if (stat.getTotalStudyDuration() != null) {
            dto.setTotalStudyDurationFormatted(formatDuration(stat.getTotalStudyDuration()));
        }

        return dto;
    }

    @Override
    public List<StudyStatDTO> convertToDTOList(List<StudyStat> stats) {
        if (CollectionUtils.isEmpty(stats)) {
            return new ArrayList<>();
        }

        List<StudyStatDTO> result = new ArrayList<>();
        for (StudyStat stat : stats) {
            result.add(convertToDTO(stat));
        }
        return result;
    }

    private StudyStat getOrCreateTodayStat(Long userId, String today) {
        LambdaQueryWrapper<StudyStat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudyStat::getUserId, userId)
                .eq(StudyStat::getStatDate, today);

        StudyStat stat = this.getOne(queryWrapper);

        if (stat == null) {
            stat = new StudyStat();
            stat.setUserId(userId);
            stat.setStatDate(today);
            stat.setLearnedCards(0);
            stat.setReviewedCards(0);
            stat.setNewCards(0);
            stat.setCorrectCount(0);
            stat.setIncorrectCount(0);
            stat.setAccuracyRate(BigDecimal.ZERO);
            stat.setStudyDuration(0L);
            stat.setTotalLearnedCards(0);
            stat.setTotalReviewedCards(0);
            stat.setTotalStudyDuration(0L);
            stat.setTodayPendingReview((int) countTodayReviewCards(userId));

            this.save(stat);
        }

        return stat;
    }

    private long countTodayReviewCards(Long userId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getUserId, userId);

        long todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000L));
        queryWrapper.le(Card::getNextReviewTime, todayStart + SpacedRepetitionAlgorithm.DAY_IN_MILLIS);

        return cardDao.selectCount(queryWrapper);
    }

    private List<DeckDTO> getRecentDecks(Long userId) {
        LambdaQueryWrapper<Deck> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Deck::getUserId, userId)
                .orderByDesc(Deck::getUpdateTime)
                .last("LIMIT 6");

        List<Deck> decks = deckDao.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(decks)) {
            return new ArrayList<>();
        }

        List<DeckDTO> result = new ArrayList<>();
        for (Deck deck : decks) {
            DeckDTO dto = new DeckDTO();
            BeanUtils.copyProperties(deck, dto);
            result.add(dto);
        }
        return result;
    }

    private List<CardDTO> getTodayReviewCardsPreview(Long userId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getUserId, userId);

        long todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000L));
        queryWrapper.le(Card::getNextReviewTime, todayStart + SpacedRepetitionAlgorithm.DAY_IN_MILLIS)
                .orderByAsc(Card::getNextReviewTime)
                .last("LIMIT 4");

        List<Card> cards = cardDao.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(cards)) {
            return new ArrayList<>();
        }

        List<CardDTO> result = new ArrayList<>();
        for (Card card : cards) {
            CardDTO dto = new CardDTO();
            BeanUtils.copyProperties(card, dto);
            dto.setIsDueToday(true);
            result.add(dto);
        }
        return result;
    }

    private List<TagDTO> getHotTags(Long userId) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getUserId, userId)
                .gt(Tag::getCardCount, 0)
                .orderByDesc(Tag::getCardCount)
                .last("LIMIT 10");

        List<Tag> tags = tagDao.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }

        List<TagDTO> result = new ArrayList<>();
        for (Tag tag : tags) {
            TagDTO dto = new TagDTO();
            BeanUtils.copyProperties(tag, dto);
            result.add(dto);
        }
        return result;
    }

    private String formatDuration(Long durationMillis) {
        if (durationMillis == null || durationMillis <= 0) {
            return "0分钟";
        }

        long seconds = durationMillis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        }
        return minutes + "分钟";
    }
}
