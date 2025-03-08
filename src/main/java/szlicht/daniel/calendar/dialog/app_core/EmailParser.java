package szlicht.daniel.calendar.dialog.app_core;

import szlicht.daniel.calendar.meeting.app_core.MeetingDto;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

class EmailParser {

    private EmailData.EmailDataBuilder dataBuilder = EmailData.builder();
    private RawEmail rawEmail;

    EmailParser(RawEmail rawEmail) {
        this.rawEmail = rawEmail;
    }

    EmailData parseEmail() {
        DialogType dialogType = DialogType.getByKeyword(rawEmail.subject());
        switch (dialogType) {
            case SOLO_MENTORING_OFFER -> includeSoloMentoringData();
            case GROUP_MENTORING_OFFER -> includeGroupMentoringData();
            case PROPOSITIONS -> includePropositionsData();
            case ARRANGE -> includeArrangeData();
            case OFFER -> includeOfferData();
            case OTHER -> {}
        }
        dataBuilder.dialogType(dialogType);
        return dataBuilder.build();
    }

    private void includeGroupMentoringData() {
        dataBuilder.content(rawEmail.content());
        dataBuilder.name(rawEmail.name());
        dataBuilder.email(rawEmail.email());
    }

    private void includeSoloMentoringData() {
        includePropositionsData();
        dataBuilder.minutes(60);
        dataBuilder.content(rawEmail.content());
    }

    private void includeArrangeData() {
        String content = rawEmail.content();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Empty or unsupported email body format.");
        }
        content = content.replace("\n", "");
        String[] elements = content.split(";");

        if (elements.length < 2) {
            throw new IllegalArgumentException("Empty or unsupported email body format.");
        }
        try {
            LocalDateTime start = LocalDateTime.parse(elements[0].split("#")[1]);
            Integer minutes = Integer.parseInt(elements[1].split("#")[1]);
            LocalDateTime end = start.plusMinutes(minutes);
            String description = elements.length == 4 ? elements[3].trim() : "";
            MeetingDto meetingDto = MeetingDto.builder()
                    .start(start)
                    .end(end)
                    .email(rawEmail.email())
                    .studentName(rawEmail.name())
                    .providedDescription(description)
                    .build();
            dataBuilder.meetingDto(meetingDto);

        } catch (DateTimeException| NumberFormatException e) {
           throw new IllegalArgumentException("Parsing failed: " + e.getMessage(), e);
        }
    }

    private void includePropositionsData() {
        Integer minutes = extractMinutes(rawEmail.subject());
        if (minutes != null) {
            dataBuilder.minutes(minutes);
        }
        dataBuilder.name(rawEmail.name());
        dataBuilder.email(rawEmail.email());
    }

    private void includeOfferData() {
        StudentStartMessageDto message = StudentStartMessageDto.builder()
                .name(rawEmail.name())
                .email(rawEmail.email())
                .story(rawEmail.content())
                .build();
        dataBuilder.studentStartMessageDto(message);
    }

    private String extractProvidedDescriptions(List<String> allLines) {
        StringBuilder result = new StringBuilder();
        boolean startAdding = false;
        for (String line : allLines) {
            if (line.startsWith(params.keywords().description())) {
                startAdding = true;
                line = line.replace(params.keywords().description(), "");
            }
            if (!startAdding || line.isBlank()) {
                continue;
            }
            result.append(line)
                    .append("\n");
        }
        return result.toString();
    }


    private Integer extractMinutes(String subject) {
        try {
            Pattern findingNumberPattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
            Matcher matcher = findingNumberPattern.matcher(subject);
            if (!matcher.find()) {
                return null;
            }
            String foundNumber = matcher.group(1);
            double value = Double.parseDouble(foundNumber.replace(",", "."));
            return (int) (value * 60);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}
