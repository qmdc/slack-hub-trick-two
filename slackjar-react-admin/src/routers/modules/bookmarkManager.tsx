import {BookOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const bookmarkManager: MenuRouteObject = {
    path: "bookmark-manager",
    label: "menu.bookmark manager",
    icon: <BookOutlined/>,
    children: [
        {
            path: "bookmark-list",
            label: "menu.bookmark list",
            icon: <BookOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/BookmarkManager")))
        }
    ] as MenuRouteObject[]
}

export default bookmarkManager;