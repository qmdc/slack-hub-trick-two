import {ProjectOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const taskDashboard: MenuRouteObject = {
    path: "task-dashboard",
    label: "menu.task dashboard",
    icon: <ProjectOutlined/>,
    hidden: true,
    element: lazyLoad(lazy(() => import("../../pages/TaskDashboard")))
}

export default taskDashboard;
