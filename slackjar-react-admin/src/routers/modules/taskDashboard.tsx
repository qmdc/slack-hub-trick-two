import {KanbanOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const taskDashboard: MenuRouteObject = {
    path: "task-dashboard",
    label: "menu.task dashboard",
    icon: <KanbanOutlined/>,
    element: lazyLoad(lazy(() => import("../../pages/TaskDashboard")))
}

export default taskDashboard;
