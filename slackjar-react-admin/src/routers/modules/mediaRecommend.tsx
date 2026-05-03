import {lazy} from "react";
import {BookOutlined} from "@ant-design/icons";
import type {MenuRouteObject} from "../router";

const MediaRecommend = lazy(() => import("../../pages/MediaRecommend"));

const mediaRecommendRoute: MenuRouteObject = {
    path: "/media-recommend",
    element: <MediaRecommend/>,
    icon: <BookOutlined/>,
    label: "menu.media_recommend",
    hidden: false,
};

export default mediaRecommendRoute;