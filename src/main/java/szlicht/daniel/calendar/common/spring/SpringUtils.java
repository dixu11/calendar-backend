package szlicht.daniel.calendar.common.spring;

import szlicht.daniel.calendar.meeting.MeetingParams;

public class SpringUtils {

    public static MeetingParams params;

    public static void initParams() {
        params = ApplicationContextProvider.getBean(MeetingParams.class);
    }
}
