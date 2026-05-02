import {
    BookOutlined,
    DashboardOutlined,
    FileTextOutlined,
    FundProjectionScreenOutlined,
    ShopOutlined,
} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const habitFormation: MenuRouteObject = {
    path: "habit-formation",
    label: "menu.habit formation",
    icon: <BookOutlined/>,
    children: [
        {
            path: "dashboard",
            label: "menu.study dashboard",
            icon: <DashboardOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/HabitFormation/StudyDashboard")))
        },
        {
            path: "deck",
            label: "menu.deck management",
            icon: <FileTextOutlined/>,
            children: [
                {
                    path: "list",
                    label: "menu.deck list",
                    icon: <FileTextOutlined/>,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/DeckList")))
                },
                {
                    path: "card/:deckId?",
                    label: "menu.card management",
                    icon: <FileTextOutlined/>,
                    hidden: true,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/CardList")))
                }
            ] as MenuRouteObject[]
        },
        {
            path: "study",
            label: "menu.card study",
            icon: <BookOutlined/>,
            children: [
                {
                    path: "review",
                    label: "menu.daily review",
                    icon: <BookOutlined/>,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/DailyReview")))
                },
                {
                    path: "new",
                    label: "menu.new cards",
                    icon: <FileTextOutlined/>,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/NewCards")))
                }
            ] as MenuRouteObject[]
        },
        {
            path: "statistics",
            label: "menu.study statistics",
            icon: <FundProjectionScreenOutlined/>,
            children: [
                {
                    path: "",
                    label: "menu.study statistics",
                    icon: <FundProjectionScreenOutlined/>,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/StudyStatistics")))
                }
            ] as MenuRouteObject[]
        },
        {
            path: "market",
            label: "menu.card market",
            icon: <ShopOutlined/>,
            children: [
                {
                    path: "explore",
                    label: "menu.market explore",
                    icon: <ShopOutlined/>,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/MarketExplore")))
                },
                {
                    path: "my-shares",
                    label: "menu.my shares",
                    icon: <FileTextOutlined/>,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/MyShares")))
                },
                {
                    path: "my-favorites",
                    label: "menu.my favorites",
                    icon: <FileTextOutlined/>,
                    element: lazyLoad(lazy(() => import("../../pages/HabitFormation/MyFavorites")))
                }
            ] as MenuRouteObject[]
        }
    ] as MenuRouteObject[]
}

export default habitFormation
