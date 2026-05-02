import {MessageOutlined} from "@ant-design/icons";
import {MenuRouteObject} from "../router";
import EmotionChat from "../../pages/EmotionChat";

const emotionChat: MenuRouteObject = {
    path: "emotion-chat",
    label: "menu.emotionChat",
    icon: <MessageOutlined/>,
    element: <EmotionChat/>
}

export default emotionChat;