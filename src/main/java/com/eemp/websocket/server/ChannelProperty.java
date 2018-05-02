package com.eemp.websocket.server;

public interface ChannelProperty {

    enum Event implements ChannelProperty {
        addChannel("addChannel"),
        removeChannel("removeChannel");
        private String key;

        private Event(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static Event getEvent(String key) {
            Event[] events = Event.values();
            for (Event event : events) {
                if (event.getKey().equals(key)) {
                    return event;
                }
            }
            return null;
        }
    }


    enum ChannelInfo implements ChannelProperty {
        test("test", true),
        test2("test2", true);
        private String key;

        private boolean isKeepAlive;

        private ChannelInfo(String key, boolean isKeepAlive) {
            this.key = key;

            this.isKeepAlive = isKeepAlive;

        }

        public String getKey() {
            return key;
        }


        public boolean isKeepAlive() {
            return isKeepAlive;
        }


        public static ChannelInfo getChannelInfo(String key) {
            ChannelInfo[] infos = ChannelInfo.values();
            for (ChannelInfo info : infos) {
                if (info.getKey().equals(key)) {
                    return info;
                }
            }
            return null;
        }
    }
}
