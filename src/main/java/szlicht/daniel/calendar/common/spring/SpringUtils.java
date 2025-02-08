package szlicht.daniel.calendar.common.spring;

import szlicht.daniel.calendar.meeting.MeetingParams;

public class SpringUtils {

    public static MeetingParams getParams(){
        return ApplicationContextProvider.getBean(MeetingParams.class);
    }
}
