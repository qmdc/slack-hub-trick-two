package com.slack.slackjarservice.taskdashboard.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 间隔重复算法工具类
 * 基于艾宾浩斯遗忘曲线和SM-2算法（Anki同款算法）
 * 
 * 核心思想：
 * 1. 根据复习质量动态调整下次复习时间
 * 2. 记得越牢的卡片，出现频率越低
 * 3. 复习质量评分（0-5分）决定间隔调整幅度
 *
 * @author system
 */
public class SpacedRepetitionAlgorithm {

    /**
     * 初始易忘因子（Ease Factor）
     * 默认值2.5，表示中等难度
     */
    public static final BigDecimal INITIAL_EASE_FACTOR = new BigDecimal("2.5");

    /**
     * 最小易忘因子
     * 防止因子过小导致复习过于频繁
     */
    public static final BigDecimal MIN_EASE_FACTOR = new BigDecimal("1.3");

    /**
     * 初始间隔天数（第一次复习间隔）
     */
    public static final BigDecimal INITIAL_INTERVAL = new BigDecimal("1.0");

    /**
     * 第二次复习间隔
     */
    public static final BigDecimal SECOND_INTERVAL = new BigDecimal("4.0");

    /**
     * 一天的毫秒数
     */
    public static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    /**
     * 复习质量评分枚举
     */
    public enum ReviewRating {
        /**
         * 0分：完全忘记 - 需要重新学习
         */
        FORGOT(0),
        /**
         * 1分：非常困难 - 几乎记不起来
         */
        VERY_HARD(1),
        /**
         * 2分：困难 - 勉强记得，错误很多
         */
        HARD(2),
        /**
         * 3分：一般 - 能想起，但需要努力
         */
        MEDIUM(3),
        /**
         * 4分：容易 - 轻松想起
         */
        EASY(4),
        /**
         * 5分：非常容易 - 立即想起，印象深刻
         */
        VERY_EASY(5);

        private final int value;

        ReviewRating(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ReviewRating fromValue(int value) {
            for (ReviewRating rating : values()) {
                if (rating.value == value) {
                    return rating;
                }
            }
            return MEDIUM;
        }
    }

    /**
     * 算法计算结果
     */
    public static class AlgorithmResult {
        /**
         * 新的间隔天数
         */
        private BigDecimal newInterval;

        /**
         * 新的易忘因子
         */
        private BigDecimal newEaseFactor;

        /**
         * 下次复习时间（毫秒时间戳）
         */
        private Long nextReviewTime;

        /**
         * 掌握程度（0-5级）
         */
        private Integer masteryLevel;

        /**
         * 此次复习是否算正确掌握
         */
        private Boolean isCorrect;

        public AlgorithmResult() {
        }

        public AlgorithmResult(BigDecimal newInterval, BigDecimal newEaseFactor, 
                               Long nextReviewTime, Integer masteryLevel, Boolean isCorrect) {
            this.newInterval = newInterval;
            this.newEaseFactor = newEaseFactor;
            this.nextReviewTime = nextReviewTime;
            this.masteryLevel = masteryLevel;
            this.isCorrect = isCorrect;
        }

        public BigDecimal getNewInterval() {
            return newInterval;
        }

        public void setNewInterval(BigDecimal newInterval) {
            this.newInterval = newInterval;
        }

        public BigDecimal getNewEaseFactor() {
            return newEaseFactor;
        }

        public void setNewEaseFactor(BigDecimal newEaseFactor) {
            this.newEaseFactor = newEaseFactor;
        }

        public Long getNextReviewTime() {
            return nextReviewTime;
        }

        public void setNextReviewTime(Long nextReviewTime) {
            this.nextReviewTime = nextReviewTime;
        }

        public Integer getMasteryLevel() {
            return masteryLevel;
        }

        public void setMasteryLevel(Integer masteryLevel) {
            this.masteryLevel = masteryLevel;
        }

        public Boolean getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }

    /**
     * 计算下次复习时间
     * 
     * @param currentInterval 当前间隔天数（可为null，表示新卡片）
     * @param currentEaseFactor 当前易忘因子（可为null，使用默认值）
     * @param reviewCount 已复习次数
     * @param rating 复习质量评分（0-5）
     * @return 算法计算结果
     */
    public static AlgorithmResult calculateNextReview(
            BigDecimal currentInterval,
            BigDecimal currentEaseFactor,
            int reviewCount,
            int rating
    ) {
        ReviewRating reviewRating = ReviewRating.fromValue(rating);
        
        BigDecimal interval = currentInterval != null ? currentInterval : BigDecimal.ZERO;
        BigDecimal easeFactor = currentEaseFactor != null ? currentEaseFactor : INITIAL_EASE_FACTOR;

        AlgorithmResult result = new AlgorithmResult();
        
        if (rating < 3) {
            result = handleFailedReview(interval, easeFactor, reviewRating);
        } else {
            result = handleSuccessfulReview(interval, easeFactor, reviewCount, reviewRating);
        }

        result.setNextReviewTime(calculateNextReviewTimestamp(result.getNewInterval()));
        result.setMasteryLevel(calculateMasteryLevel(rating, reviewCount));
        result.setIsCorrect(rating >= 3);

        return result;
    }

    /**
     * 处理复习失败（评分 < 3）
     * 此时需要重置或缩短复习间隔
     */
    private static AlgorithmResult handleFailedReview(
            BigDecimal currentInterval,
            BigDecimal currentEaseFactor,
            ReviewRating rating
    ) {
        AlgorithmResult result = new AlgorithmResult();
        
        BigDecimal newEaseFactor = adjustEaseFactor(currentEaseFactor, rating);
        result.setNewEaseFactor(newEaseFactor);

        if (rating.getValue() == 0) {
            result.setNewInterval(BigDecimal.ONE);
        } else {
            BigDecimal newInterval = currentInterval.multiply(new BigDecimal("0.5"))
                    .setScale(1, RoundingMode.HALF_UP);
            result.setNewInterval(newInterval.max(BigDecimal.ONE));
        }

        return result;
    }

    /**
     * 处理复习成功（评分 >= 3）
     * 此时根据复习次数和质量调整间隔
     */
    private static AlgorithmResult handleSuccessfulReview(
            BigDecimal currentInterval,
            BigDecimal currentEaseFactor,
            int reviewCount,
            ReviewRating rating
    ) {
        AlgorithmResult result = new AlgorithmResult();

        BigDecimal newEaseFactor = adjustEaseFactor(currentEaseFactor, rating);
        result.setNewEaseFactor(newEaseFactor);

        BigDecimal newInterval;
        if (reviewCount == 0) {
            newInterval = INITIAL_INTERVAL;
        } else if (reviewCount == 1) {
            newInterval = SECOND_INTERVAL;
        } else {
            newInterval = currentInterval.multiply(newEaseFactor)
                    .setScale(1, RoundingMode.HALF_UP);
        }

        if (rating == ReviewRating.EASY) {
            newInterval = newInterval.multiply(new BigDecimal("1.3"))
                    .setScale(1, RoundingMode.HALF_UP);
        } else if (rating == ReviewRating.VERY_EASY) {
            newInterval = newInterval.multiply(new BigDecimal("1.5"))
                    .setScale(1, RoundingMode.HALF_UP);
        }

        result.setNewInterval(newInterval);

        return result;
    }

    /**
     * 调整易忘因子（Ease Factor）
     * 公式：EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
     * 其中 q 是复习评分
     */
    private static BigDecimal adjustEaseFactor(BigDecimal currentEaseFactor, ReviewRating rating) {
        int q = rating.getValue();
        BigDecimal delta = new BigDecimal("0.1")
                .subtract(
                        new BigDecimal(5 - q)
                                .multiply(
                                        new BigDecimal("0.08")
                                                .add(new BigDecimal(5 - q).multiply(new BigDecimal("0.02")))
                                )
                );

        BigDecimal newEaseFactor = currentEaseFactor.add(delta)
                .setScale(2, RoundingMode.HALF_UP);

        return newEaseFactor.max(MIN_EASE_FACTOR);
    }

    /**
     * 计算下次复习的时间戳
     */
    private static Long calculateNextReviewTimestamp(BigDecimal intervalDays) {
        long currentTime = System.currentTimeMillis();
        long intervalMillis = intervalDays.multiply(new BigDecimal(DAY_IN_MILLIS))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
        return currentTime + intervalMillis;
    }

    /**
     * 根据复习评分和复习次数计算掌握程度
     * 掌握程度范围：0-5级
     */
    private static Integer calculateMasteryLevel(int rating, int reviewCount) {
        if (rating < 3) {
            return Math.max(0, rating);
        } else {
            int baseLevel = rating;
            int bonusLevel = Math.min(2, reviewCount / 3);
            return Math.min(5, baseLevel + bonusLevel);
        }
    }

    /**
     * 初始化新卡片的参数
     */
    public static void initializeNewCard(com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.Card card) {
        card.setMasteryLevel(0);
        card.setReviewCount(0);
        card.setCorrectCount(0);
        card.setIncorrectCount(0);
        card.setConsecutiveCorrectCount(0);
        card.setIntervalDays(BigDecimal.ZERO);
        card.setDifficulty(new BigDecimal("2.5"));
        card.setEaseFactor(INITIAL_EASE_FACTOR);
        card.setLastReviewTime(null);
        card.setNextReviewTime(calculateNextReviewTimestamp(INITIAL_INTERVAL));
        card.setIsImportant(0);
    }

    /**
     * 判断卡片是否需要今天复习
     */
    public static boolean isCardDueToday(Long nextReviewTime) {
        if (nextReviewTime == null) {
            return true;
        }
        long todayStart = getTodayStartMillis();
        return nextReviewTime <= todayStart + DAY_IN_MILLIS;
    }

    /**
     * 获取今天开始的时间戳
     */
    private static long getTodayStartMillis() {
        LocalDateTime todayStart = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return todayStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 计算艾宾浩斯遗忘曲线的复习间隔
     * 经典间隔：1天、2天、4天、7天、15天、30天、60天...
     */
    public static BigDecimal getEbbinghausInterval(int reviewSequence) {
        int[] ebbinghausIntervals = {1, 2, 4, 7, 15, 30, 60, 90, 120, 180};
        
        if (reviewSequence < 0) {
            return BigDecimal.ONE;
        }
        
        if (reviewSequence < ebbinghausIntervals.length) {
            return new BigDecimal(ebbinghausIntervals[reviewSequence]);
        }
        
        return new BigDecimal(ebbinghausIntervals[ebbinghausIntervals.length - 1]);
    }
}
