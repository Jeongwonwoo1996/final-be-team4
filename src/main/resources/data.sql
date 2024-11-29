# TRUNCATE TABLE voice_style;
# ALTER TABLE voice_style AUTO_INCREMENT = 1;

DELETE
FROM voice_style;

# CREATE TABLE voice_style
# (
#     id            INT AUTO_INCREMENT PRIMARY KEY,   -- 테이블의 기본 키
#     country       VARCHAR(255)            NOT NULL, -- 언어 또는 국가 정보
#     language_code VARCHAR(50)             NOT NULL, -- 언어 코드
#     voice_type    VARCHAR(100)            NOT NULL, -- 음성 유형
#     voice_name    VARCHAR(255)            NOT NULL, -- 음성 이름
#     gender        ENUM ('male', 'female') NOT NULL, -- 성별 (남성 또는 여성)
#     personality   VARCHAR(255),                     -- 성격 (설명)
#     is_visible    BOOLEAN DEFAULT TRUE              -- 가시성 여부 (1 또는 0)
# );


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아프리칸스어(남아프리카 공화국)', 'af-ZA', 'af-ZA-Standard-A',
        '아프리칸스어(남아프리카 공화국)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Standard-A',
        '아랍어1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Standard-B',
        '아랍어2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Standard-C',
        '아랍어3', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Standard-D',
        '아랍어4', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Wavenet-A',
        '아랍어5', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Wavenet-B',
        '아랍어6', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Wavenet-C',
        '아랍어7', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아랍어', 'ar-XA', 'ar-XA-Wavenet-D',
        '아랍어8', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('바스크어(스페인)', 'eu-ES', 'eu-ES-Standard-A',
        '바스크어(스페인)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('바스크어(스페인)', 'eu-ES', 'eu-ES-Standard-B',
        '바스크어(스페인)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Standard-A',
        '벵골어(인도)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Standard-B',
        '벵골어(인도)2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Standard-C',
        '벵골어(인도)3', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Standard-D',
        '벵골어(인도)4', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Wavenet-A',
        '벵골어(인도)5', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Wavenet-B',
        '벵골어(인도)6', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Wavenet-C',
        '벵골어(인도)7', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('벵골어(인도)', 'bn-IN', 'bn-IN-Wavenet-D',
        '벵골어(인도)8', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('불가리아어(불가리아)', 'bg-BG', 'bg-BG-Standard-A',
        '불가리아어(불가리아)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('불가리아어(불가리아)', 'bg-BG', 'bg-BG-Standard-B',
        '불가리아어(불가리아)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('카탈로니아어(스페인)', 'ca-ES', 'ca-ES-Standard-A',
        '카탈로니아어(스페인)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('카탈로니아어(스페인)', 'ca-ES', 'ca-ES-Standard-B',
        '카탈로니아어(스페인)2', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어(홍콩)', 'yue-HK', 'yue-HK-Standard-A',
        '중국어(홍콩)1', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어(홍콩)', 'yue-HK', 'yue-HK-Standard-B',
        '중국어(홍콩)2', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어(홍콩)', 'yue-HK', 'yue-HK-Standard-C',
        '중국어(홍콩)3', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어(홍콩)', 'yue-HK', 'yue-HK-Standard-D',
        '중국어(홍콩)4', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('체코어(체코)', 'cs-CZ', 'cs-CZ-Standard-A',
        '체코어(체코)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('체코어(체코)', 'cs-CZ', 'cs-CZ-Wavenet-A',
        '체코어(체코)2', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Neural2-D',
        '덴마크어(덴마크)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Standard-A',
        '덴마크어(덴마크)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Standard-C',
        '덴마크어(덴마크)3', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Standard-D',
        '덴마크어(덴마크)4', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Standard-E',
        '덴마크어(덴마크)5', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Standard-F',
        '덴마크어(덴마크)6', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Standard-G',
        '덴마크어(덴마크)7', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Wavenet-A',
        '덴마크어(덴마크)8', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Wavenet-C',
        '덴마크어(덴마크)9', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Wavenet-D',
        '덴마크어(덴마크)10', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('덴마크어(덴마크)', 'da-DK', 'da-DK-Wavenet-E',
        '덴마크어(덴마크)11', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(벨기에)', 'nl-BE', 'nl-BE-Standard-A',
        '네덜란드어(벨기에)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(벨기에)', 'nl-BE', 'nl-BE-Standard-B',
        '네덜란드어(벨기에)2', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(벨기에)', 'nl-BE', 'nl-BE-Standard-C',
        '네덜란드어(벨기에)3', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(벨기에)', 'nl-BE', 'nl-BE-Standard-D',
        '네덜란드어(벨기에)4', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(벨기에)', 'nl-BE', 'nl-BE-Wavenet-A',
        '네덜란드어(벨기에)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(벨기에)', 'nl-BE', 'nl-BE-Wavenet-B',
        '네덜란드어(벨기에)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Standard-A',
        '네덜란드어(네덜란드)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Standard-B',
        '네덜란드어(네덜란드)2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Standard-C',
        '네덜란드어(네덜란드)3', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Standard-D',
        '네덜란드어(네덜란드)4', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Standard-E',
        '네덜란드어(네덜란드)5', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Standard-F',
        '네덜란드어(네덜란드)6', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Standard-G',
        '네덜란드어(네덜란드)7', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Wavenet-A',
        '네덜란드어(네덜란드)8', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Wavenet-B',
        '네덜란드어(네덜란드)9', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Wavenet-C',
        '네덜란드어(네덜란드)10', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Wavenet-D',
        '네덜란드어(네덜란드)11', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('네덜란드어(네덜란드)', 'nl-NL', 'nl-NL-Wavenet-E',
        '네덜란드어(네덜란드)12', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Neural2-A',
        '영어(호주)1', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Neural2-B',
        '영어(호주)2', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Neural2-C',
        '영어(호주)3', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Neural2-D',
        '영어(호주)4', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-News-E',
        '영어(호주)5', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-News-F',
        '영어(호주)6', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-News-G',
        '영어(호주)7', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Polyglot-1',
        '영어(호주)8', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Standard-A',
        '영어(호주)9', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Standard-B',
        '영어(호주)10', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Standard-C',
        '영어(호주)11', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Standard-D',
        '영어(호주)12', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Wavenet-A',
        '영어(호주)13', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Wavenet-B',
        '영어(호주)14', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Wavenet-C',
        '영어(호주)15', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(호주)', 'en-AU', 'en-AU-Wavenet-D',
        '영어(호주)16', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Journey-D',
        '영어(인도)1', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Journey-F',
        '영어(인도)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Neural2-A',
        '영어(인도)3', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Neural2-B',
        '영어(인도)4', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Neural2-C',
        '영어(인도)5', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Neural2-D',
        '영어(인도)6', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Standard-A',
        '영어(인도)7', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Standard-B',
        '영어(인도)8', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Standard-C',
        '영어(인도)9', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Standard-D',
        '영어(인도)10', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Standard-E',
        '영어(인도)11', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Standard-F',
        '영어(인도)12', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Wavenet-A',
        '영어(인도)13', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Wavenet-B',
        '영어(인도)14', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Wavenet-C',
        '영어(인도)15', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Wavenet-D',
        '영어(인도)16', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Wavenet-E',
        '영어(인도)17', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(인도)', 'en-IN', 'en-IN-Wavenet-F',
        '영어(인도)18', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Journey-D',
        '영어(영국)1', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Journey-F',
        '영어(영국)2', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Neural2-A',
        '영어(영국)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Neural2-B',
        '영어(영국)4', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Neural2-C',
        '영어(영국)5', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Neural2-D',
        '영어(영국)6', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Neural2-F',
        '영어(영국)7', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-News-G',
        '영어(영국)8', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-News-H',
        '영어(영국)9', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-News-I',
        '영어(영국)10', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-News-J',
        '영어(영국)11', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-News-K',
        '영어(영국)12', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-News-L',
        '영어(영국)13', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-News-M',
        '영어(영국)14', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Standard-A',
        '영어(영국)15', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Standard-B',
        '영어(영국)16', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Standard-C',
        '영어(영국)17', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Standard-D',
        '영어(영국)18', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Standard-F',
        '영어(영국)19', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Studio-B',
        '영어(영국)20', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Studio-C',
        '영어(영국)21', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Wavenet-A',
        '영어(영국)22', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Wavenet-B',
        '영어(영국)23', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Wavenet-C',
        '영어(영국)24', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Wavenet-D',
        '영어(영국)25', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(영국)', 'en-GB', 'en-GB-Wavenet-F',
        '영어(영국)26', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Casual-K',
        '영어(미국)1', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Journey-D',
        '영어(미국)2', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Journey-F',
        '영어(미국)3', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Journey-O',
        '영어(미국)4', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-A',
        '영어(미국)5', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-C',
        '영어(미국)6', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-D',
        '영어(미국)7', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-E',
        '영어(미국)8', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-F',
        '영어(미국)9', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-G',
        '영어(미국)10', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-H',
        '영어(미국)11', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-I',
        '영어(미국)12', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Neural2-J',
        '영어(미국)13', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-News-K',
        '영어(미국)14', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-News-L',
        '영어(미국)15', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-News-N',
        '영어(미국)16', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Polyglot-1',
        '영어(미국)17', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-A',
        '영어(미국)18', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-B',
        '영어(미국)19', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-C',
        '영어(미국)20', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-D',
        '영어(미국)21', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-E',
        '영어(미국)22', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-F',
        '영어(미국)23', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-G',
        '영어(미국)24', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-H',
        '영어(미국)25', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-I',
        '영어(미국)26', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Standard-J',
        '영어(미국)27', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Studio-O',
        '영어(미국)28', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Studio-Q',
        '영어(미국)29', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-A',
        '영어(미국)30', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-B',
        '영어(미국)31', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-C',
        '영어(미국)32', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-D',
        '영어(미국)33', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-E',
        '영어(미국)34', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-F',
        '영어(미국)35', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-G',
        '영어(미국)36', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-H',
        '영어(미국)37', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-I',
        '영어(미국)38', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('영어(미국)', 'en-US', 'en-US-Wavenet-J',
        '영어(미국)39', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Standard-A',
        '필리핀어(필리핀)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Standard-B',
        '필리핀어(필리핀)2', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Standard-C',
        '필리핀어(필리핀)3', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Standard-D',
        '필리핀어(필리핀)4', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Wavenet-A',
        '필리핀어(필리핀)5', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Wavenet-B',
        '필리핀어(필리핀)6', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Wavenet-C',
        '필리핀어(필리핀)7', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-PH-Wavenet-D',
        '필리핀어(필리핀)8', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-ph-Neural2-A',
        '필리핀어(필리핀)9', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('필리핀어(필리핀)', 'fil-PH', 'fil-ph-Neural2-D',
        '필리핀어(필리핀)10', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('핀란드어(핀란드)', 'fi-FI', 'fi-FI-Standard-A',
        '핀란드어(핀란드)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('핀란드어(핀란드)', 'fi-FI', 'fi-FI-Standard-B',
        '핀란드어(핀란드)2', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('핀란드어(핀란드)', 'fi-FI', 'fi-FI-Wavenet-A',
        '핀란드어(핀란드)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Journey-D',
        '프랑스어(캐나다)1', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Journey-F',
        '프랑스어(캐나다)2', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Neural2-A',
        '프랑스어(캐나다)3', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Neural2-B',
        '프랑스어(캐나다)4', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Neural2-C',
        '프랑스어(캐나다)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Neural2-D',
        '프랑스어(캐나다)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Standard-A',
        '프랑스어(캐나다)7', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Standard-B',
        '프랑스어(캐나다)8', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Standard-C',
        '프랑스어(캐나다)9', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Standard-D',
        '프랑스어(캐나다)10', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Wavenet-A',
        '프랑스어(캐나다)11', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Wavenet-B',
        '프랑스어(캐나다)12', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Wavenet-C',
        '프랑스어(캐나다)13', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(캐나다)', 'fr-CA', 'fr-CA-Wavenet-D',
        '프랑스어(캐나다)14', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Journey-D',
        '프랑스어(프랑스)1', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Journey-F',
        '프랑스어(프랑스)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Neural2-A',
        '프랑스어(프랑스)3', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Neural2-B',
        '프랑스어(프랑스)4', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Neural2-C',
        '프랑스어(프랑스)5', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Neural2-D',
        '프랑스어(프랑스)6', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Neural2-E',
        '프랑스어(프랑스)7', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Polyglot-1',
        '프랑스어(프랑스)8', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Standard-A',
        '프랑스어(프랑스)9', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Standard-B',
        '프랑스어(프랑스)10', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Standard-C',
        '프랑스어(프랑스)11', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Standard-D',
        '프랑스어(프랑스)12', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Standard-E',
        '프랑스어(프랑스)13', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Standard-F',
        '프랑스어(프랑스)14', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Standard-G',
        '프랑스어(프랑스)15', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Studio-A',
        '프랑스어(프랑스)16', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Studio-D',
        '프랑스어(프랑스)17', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Wavenet-A',
        '프랑스어(프랑스)18', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Wavenet-B',
        '프랑스어(프랑스)19', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Wavenet-C',
        '프랑스어(프랑스)20', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Wavenet-D',
        '프랑스어(프랑스)21', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Wavenet-E',
        '프랑스어(프랑스)22', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Wavenet-F',
        '프랑스어(프랑스)23', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('프랑스어(프랑스)', 'fr-FR', 'fr-FR-Wavenet-G',
        '프랑스어(프랑스)24', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('갈리시아어(스페인)', 'gl-ES', 'gl-ES-Standard-A',
        '갈리시아어(스페인)1', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('갈리시아어(스페인)', 'gl-ES', 'gl-ES-Standard-B',
        '갈리시아어(스페인)2', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Journey-D',
        '독일어(독일)1', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Journey-F',
        '독일어(독일)2', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Neural2-A',
        '독일어(독일)3', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Neural2-B',
        '독일어(독일)4', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Neural2-C',
        '독일어(독일)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Neural2-D',
        '독일어(독일)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Neural2-F',
        '독일어(독일)7', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Polyglot-1',
        '독일어(독일)8', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-A',
        '독일어(독일)9', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-B',
        '독일어(독일)10', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-C',
        '독일어(독일)11', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-D',
        '독일어(독일)12', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-E',
        '독일어(독일)13', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-F',
        '독일어(독일)14', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-G',
        '독일어(독일)15', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Standard-H',
        '독일어(독일)16', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Studio-B',
        '독일어(독일)17', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Studio-C',
        '독일어(독일)18', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-A',
        '독일어(독일)19', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-B',
        '독일어(독일)20', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-C',
        '독일어(독일)21', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-D',
        '독일어(독일)22', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-E',
        '독일어(독일)23', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-F',
        '독일어(독일)24', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-G',
        '독일어(독일)25', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('독일어(독일)', 'de-DE', 'de-DE-Wavenet-H',
        '독일어(독일)26', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('그리스어(그리스)', 'el-GR', 'el-GR-Standard-A',
        '그리스어(그리스)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('그리스어(그리스)', 'el-GR', 'el-GR-Standard-B',
        '그리스어(그리스)2', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('그리스어(그리스)', 'el-GR', 'el-GR-Wavenet-A',
        '그리스어(그리스)3', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Standard-A',
        '구자라트어(인도)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Standard-B',
        '구자라트어(인도)2', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Standard-C',
        '구자라트어(인도)3', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Standard-D',
        '구자라트어(인도)4', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Wavenet-A',
        '구자라트어(인도)5', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Wavenet-B',
        '구자라트어(인도)6', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Wavenet-C',
        '구자라트어(인도)7', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('구자라트어(인도)', 'gu-IN', 'gu-IN-Wavenet-D',
        '구자라트어(인도)8', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Standard-A',
        '히브리어(이스라엘)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Standard-B',
        '히브리어(이스라엘)2', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Standard-C',
        '히브리어(이스라엘)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Standard-D',
        '히브리어(이스라엘)4', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Wavenet-A',
        '히브리어(이스라엘)5', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Wavenet-B',
        '히브리어(이스라엘)6', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Wavenet-C',
        '히브리어(이스라엘)7', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('히브리어(이스라엘)', 'he-IL', 'he-IL-Wavenet-D',
        '히브리어(이스라엘)8', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Neural2-A',
        '힌디어(인도)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Neural2-B',
        '힌디어(인도)2', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Neural2-C',
        '힌디어(인도)3', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Neural2-D',
        '힌디어(인도)4', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Standard-A',
        '힌디어(인도)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Standard-B',
        '힌디어(인도)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Standard-C',
        '힌디어(인도)7', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Standard-D',
        '힌디어(인도)8', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Standard-E',
        '힌디어(인도)9', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Standard-F',
        '힌디어(인도)10', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Wavenet-A',
        '힌디어(인도)11', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Wavenet-B',
        '힌디어(인도)12', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Wavenet-C',
        '힌디어(인도)13', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Wavenet-D',
        '힌디어(인도)14', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Wavenet-E',
        '힌디어(인도)15', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('힌디어(인도)', 'hi-IN', 'hi-IN-Wavenet-F',
        '힌디어(인도)16', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('헝가리어(헝가리)', 'hu-HU', 'hu-HU-Standard-A',
        '헝가리어(헝가리)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('헝가리어(헝가리)', 'hu-HU', 'hu-HU-Standard-B',
        '헝가리어(헝가리)2', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('헝가리어(헝가리)', 'hu-HU', 'hu-HU-Wavenet-A',
        '헝가리어(헝가리)3', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아이슬란드어(아이슬란드)', 'is-IS', 'is-IS-Standard-A',
        '아이슬란드어(아이슬란드)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('아이슬란드어(아이슬란드)', 'is-IS', 'is-IS-Standard-B',
        '아이슬란드어(아이슬란드)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Standard-A',
        '인도네시아어(인도네시아)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Standard-B',
        '인도네시아어(인도네시아)2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Standard-C',
        '인도네시아어(인도네시아)3', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Standard-D',
        '인도네시아어(인도네시아)4', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Wavenet-A',
        '인도네시아어(인도네시아)5', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Wavenet-B',
        '인도네시아어(인도네시아)6', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Wavenet-C',
        '인도네시아어(인도네시아)7', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('인도네시아어(인도네시아)', 'id-ID', 'id-ID-Wavenet-D',
        '인도네시아어(인도네시아)8', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Journey-D',
        '이탈리아어(이탈리아)1', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Journey-F',
        '이탈리아어(이탈리아)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Neural2-A',
        '이탈리아어(이탈리아)3', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Neural2-C',
        '이탈리아어(이탈리아)4', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Standard-A',
        '이탈리아어(이탈리아)5', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Standard-B',
        '이탈리아어(이탈리아)6', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Standard-C',
        '이탈리아어(이탈리아)7', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Standard-D',
        '이탈리아어(이탈리아)8', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Wavenet-A',
        '이탈리아어(이탈리아)9', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Wavenet-B',
        '이탈리아어(이탈리아)10', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Wavenet-C',
        '이탈리아어(이탈리아)11', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('이탈리아어(이탈리아)', 'it-IT', 'it-IT-Wavenet-D',
        '이탈리아어(이탈리아)12', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Neural2-B',
        '일본어(일본)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Neural2-C',
        '일본어(일본)2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Neural2-D',
        '일본어(일본)3', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Standard-A',
        '일본어(일본)4', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Standard-B',
        '일본어(일본)5', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Standard-C',
        '일본어(일본)6', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Standard-D',
        '일본어(일본)7', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Wavenet-A',
        '일본어(일본)8', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Wavenet-B',
        '일본어(일본)9', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Wavenet-C',
        '일본어(일본)10', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('일본어(일본)', 'ja-JP', 'ja-JP-Wavenet-D',
        '일본어(일본)11', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Standard-A',
        '칸나다어(인도)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Standard-B',
        '칸나다어(인도)2', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Standard-C',
        '칸나다어(인도)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Standard-D',
        '칸나다어(인도)4', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Wavenet-A',
        '칸나다어(인도)5', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Wavenet-B',
        '칸나다어(인도)6', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Wavenet-C',
        '칸나다어(인도)7', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('칸나다어(인도)', 'kn-IN', 'kn-IN-Wavenet-D',
        '칸나다어(인도)8', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Neural2-A',
        '한국어(대한민국)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Neural2-B',
        '한국어(대한민국)2', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Neural2-C',
        '한국어(대한민국)3', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Standard-A',
        '한국어(대한민국)4', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Standard-B',
        '한국어(대한민국)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Standard-C',
        '한국어(대한민국)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Standard-D',
        '한국어(대한민국)7', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Wavenet-A',
        '한국어(대한민국)8', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Wavenet-B',
        '한국어(대한민국)9', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Wavenet-C',
        '한국어(대한민국)10', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('한국어(대한민국)', 'ko-KR', 'ko-KR-Wavenet-D',
        '한국어(대한민국)11', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('라트비아어(라트비아)', 'lv-LV', 'lv-LV-Standard-A',
        '라트비아어(라트비아)1', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('라트비아어(라트비아)', 'lv-LV', 'lv-LV-Standard-B',
        '라트비아어(라트비아)2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('리투아니아어(리투아니아)', 'lt-LT', 'lt-LT-Standard-A',
        '리투아니아어(리투아니아)1', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Standard-A',
        '말레이어(말레이시아)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Standard-B',
        '말레이어(말레이시아)2', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Standard-C',
        '말레이어(말레이시아)3', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Standard-D',
        '말레이어(말레이시아)4', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Wavenet-A',
        '말레이어(말레이시아)5', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Wavenet-B',
        '말레이어(말레이시아)6', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Wavenet-C',
        '말레이어(말레이시아)7', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말레이어(말레이시아)', 'ms-MY', 'ms-MY-Wavenet-D',
        '말레이어(말레이시아)8', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Standard-A',
        '말라얄람어(인도)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Standard-B',
        '말라얄람어(인도)2', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Standard-C',
        '말라얄람어(인도)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Standard-D',
        '말라얄람어(인도)4', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Wavenet-A',
        '말라얄람어(인도)5', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Wavenet-B',
        '말라얄람어(인도)6', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Wavenet-C',
        '말라얄람어(인도)7', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('말라얄람어(인도)', 'ml-IN', 'ml-IN-Wavenet-D',
        '말라얄람어(인도)8', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Standard-A',
        '중국어1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Standard-B',
        '중국어2', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Standard-C',
        '중국어3', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Standard-D',
        '중국어4', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Wavenet-A',
        '중국어5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Wavenet-B',
        '중국어6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Wavenet-C',
        '중국어7', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-CN', 'cmn-CN-Wavenet-D',
        '중국어8', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-TW', 'cmn-TW-Standard-A',
        '중국어9', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-TW', 'cmn-TW-Standard-B',
        '중국어10', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-TW', 'cmn-TW-Standard-C',
        '중국어11', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-TW', 'cmn-TW-Wavenet-A',
        '중국어12', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-TW', 'cmn-TW-Wavenet-B',
        '중국어13', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('중국어', 'cmn-TW', 'cmn-TW-Wavenet-C',
        '중국어14', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('마라티어(인도)', 'mr-IN', 'mr-IN-Standard-A',
        '마라티어(인도)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('마라티어(인도)', 'mr-IN', 'mr-IN-Standard-B',
        '마라티어(인도)2', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('마라티어(인도)', 'mr-IN', 'mr-IN-Standard-C',
        '마라티어(인도)3', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('마라티어(인도)', 'mr-IN', 'mr-IN-Wavenet-A',
        '마라티어(인도)4', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('마라티어(인도)', 'mr-IN', 'mr-IN-Wavenet-B',
        '마라티어(인도)5', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('마라티어(인도)', 'mr-IN', 'mr-IN-Wavenet-C',
        '마라티어(인도)6', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Standard-A',
        '노르웨이어(노르웨이)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Standard-B',
        '노르웨이어(노르웨이)2', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Standard-C',
        '노르웨이어(노르웨이)3', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Standard-D',
        '노르웨이어(노르웨이)4', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Standard-E',
        '노르웨이어(노르웨이)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Standard-F',
        '노르웨이어(노르웨이)6', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Standard-G',
        '노르웨이어(노르웨이)7', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Wavenet-A',
        '노르웨이어(노르웨이)8', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Wavenet-B',
        '노르웨이어(노르웨이)9', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Wavenet-C',
        '노르웨이어(노르웨이)10', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Wavenet-D',
        '노르웨이어(노르웨이)11', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('노르웨이어(노르웨이)', 'nb-NO', 'nb-NO-Wavenet-E',
        '노르웨이어(노르웨이)12', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Standard-A',
        '폴란드어(폴란드)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Standard-B',
        '폴란드어(폴란드)2', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Standard-C',
        '폴란드어(폴란드)3', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Standard-D',
        '폴란드어(폴란드)4', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Standard-E',
        '폴란드어(폴란드)5', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Wavenet-A',
        '폴란드어(폴란드)6', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Wavenet-B',
        '폴란드어(폴란드)7', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Wavenet-C',
        '폴란드어(폴란드)8', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Wavenet-D',
        '폴란드어(폴란드)9', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('폴란드어(폴란드)', 'pl-PL', 'pl-PL-Wavenet-E',
        '폴란드어(폴란드)10', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Neural2-A',
        '포르투갈어(브라질)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Neural2-B',
        '포르투갈어(브라질)2', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Neural2-C',
        '포르투갈어(브라질)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Standard-A',
        '포르투갈어(브라질)4', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Standard-B',
        '포르투갈어(브라질)5', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Standard-C',
        '포르투갈어(브라질)6', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Standard-D',
        '포르투갈어(브라질)7', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Standard-E',
        '포르투갈어(브라질)8', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Wavenet-A',
        '포르투갈어(브라질)9', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Wavenet-B',
        '포르투갈어(브라질)10', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Wavenet-C',
        '포르투갈어(브라질)11', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Wavenet-D',
        '포르투갈어(브라질)12', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(브라질)', 'pt-BR', 'pt-BR-Wavenet-E',
        '포르투갈어(브라질)13', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Standard-A',
        '포르투갈어(포르투갈)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Standard-B',
        '포르투갈어(포르투갈)2', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Standard-C',
        '포르투갈어(포르투갈)3', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Standard-D',
        '포르투갈어(포르투갈)4', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Standard-E',
        '포르투갈어(포르투갈)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Standard-F',
        '포르투갈어(포르투갈)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Wavenet-A',
        '포르투갈어(포르투갈)7', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Wavenet-B',
        '포르투갈어(포르투갈)8', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Wavenet-C',
        '포르투갈어(포르투갈)9', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('포르투갈어(포르투갈)', 'pt-PT', 'pt-PT-Wavenet-D',
        '포르투갈어(포르투갈)10', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Standard-A',
        '펀자브어(인도)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Standard-B',
        '펀자브어(인도)2', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Standard-C',
        '펀자브어(인도)3', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Standard-D',
        '펀자브어(인도)4', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Wavenet-A',
        '펀자브어(인도)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Wavenet-B',
        '펀자브어(인도)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Wavenet-C',
        '펀자브어(인도)7', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('펀자브어(인도)', 'pa-IN', 'pa-IN-Wavenet-D',
        '펀자브어(인도)8', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('루마니아어(루마니아)', 'ro-RO', 'ro-RO-Standard-A',
        '루마니아어(루마니아)1', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('루마니아어(루마니아)', 'ro-RO', 'ro-RO-Standard-B',
        '루마니아어(루마니아)2', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('루마니아어(루마니아)', 'ro-RO', 'ro-RO-Wavenet-A',
        '루마니아어(루마니아)3', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Standard-A',
        '러시아어(러시아)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Standard-B',
        '러시아어(러시아)2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Standard-C',
        '러시아어(러시아)3', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Standard-D',
        '러시아어(러시아)4', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Standard-E',
        '러시아어(러시아)5', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Wavenet-A',
        '러시아어(러시아)6', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Wavenet-B',
        '러시아어(러시아)7', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Wavenet-C',
        '러시아어(러시아)8', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Wavenet-D',
        '러시아어(러시아)9', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('러시아어(러시아)', 'ru-RU', 'ru-RU-Wavenet-E',
        '러시아어(러시아)10', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('세르비아어(키릴 자모)', 'sr-RS', 'sr-RS-Standard-A',
        '세르비아어(키릴 자모)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('슬로바키아어(슬로바키아)', 'sk-SK', 'sk-SK-Standard-A',
        '슬로바키아어(슬로바키아)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('슬로바키아어(슬로바키아)', 'sk-SK', 'sk-SK-Standard-B',
        '슬로바키아어(슬로바키아)2', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('슬로바키아어(슬로바키아)', 'sk-SK', 'sk-SK-Wavenet-A',
        '슬로바키아어(슬로바키아)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Neural2-A',
        '스페인어(스페인)1', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Neural2-B',
        '스페인어(스페인)2', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Neural2-C',
        '스페인어(스페인)3', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Neural2-D',
        '스페인어(스페인)4', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Neural2-E',
        '스페인어(스페인)5', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Neural2-F',
        '스페인어(스페인)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Polyglot-1',
        '스페인어(스페인)7', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Standard-A',
        '스페인어(스페인)8', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Standard-B',
        '스페인어(스페인)9', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Standard-C',
        '스페인어(스페인)10', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Standard-D',
        '스페인어(스페인)11', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Standard-E',
        '스페인어(스페인)12', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Standard-F',
        '스페인어(스페인)13', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Studio-C',
        '스페인어(스페인)14', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Studio-F',
        '스페인어(스페인)15', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Wavenet-B',
        '스페인어(스페인)16', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Wavenet-C',
        '스페인어(스페인)17', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Wavenet-D',
        '스페인어(스페인)18', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Wavenet-E',
        '스페인어(스페인)19', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(스페인)', 'es-ES', 'es-ES-Wavenet-F',
        '스페인어(스페인)20', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Journey-D',
        '스페인어(미국)1', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Journey-F',
        '스페인어(미국)2', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Neural2-A',
        '스페인어(미국)3', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Neural2-B',
        '스페인어(미국)4', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Neural2-C',
        '스페인어(미국)5', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-News-D',
        '스페인어(미국)6', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-News-E',
        '스페인어(미국)7', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-News-F',
        '스페인어(미국)8', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-News-G',
        '스페인어(미국)9', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Polyglot-1',
        '스페인어(미국)10', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Standard-A',
        '스페인어(미국)11', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Standard-B',
        '스페인어(미국)12', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Standard-C',
        '스페인어(미국)13', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Studio-B',
        '스페인어(미국)14', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Wavenet-A',
        '스페인어(미국)15', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Wavenet-B',
        '스페인어(미국)16', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스페인어(미국)', 'es-US', 'es-US-Wavenet-C',
        '스페인어(미국)17', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Standard-A',
        '스웨덴어(스웨덴)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Standard-B',
        '스웨덴어(스웨덴)2', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Standard-C',
        '스웨덴어(스웨덴)3', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Standard-D',
        '스웨덴어(스웨덴)4', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Standard-E',
        '스웨덴어(스웨덴)5', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Standard-F',
        '스웨덴어(스웨덴)6', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Standard-G',
        '스웨덴어(스웨덴)7', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Wavenet-A',
        '스웨덴어(스웨덴)8', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Wavenet-B',
        '스웨덴어(스웨덴)9', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Wavenet-C',
        '스웨덴어(스웨덴)10', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Wavenet-D',
        '스웨덴어(스웨덴)11', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('스웨덴어(스웨덴)', 'sv-SE', 'sv-SE-Wavenet-E',
        '스웨덴어(스웨덴)12', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Standard-A',
        '타밀어(인도)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Standard-B',
        '타밀어(인도)2', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Standard-C',
        '타밀어(인도)3', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Standard-D',
        '타밀어(인도)4', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Wavenet-A',
        '타밀어(인도)5', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Wavenet-B',
        '타밀어(인도)6', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Wavenet-C',
        '타밀어(인도)7', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('타밀어(인도)', 'ta-IN', 'ta-IN-Wavenet-D',
        '타밀어(인도)8', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('텔루구어(인도)', 'te-IN', 'te-IN-Standard-A',
        '텔루구어(인도)1', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('텔루구어(인도)', 'te-IN', 'te-IN-Standard-B',
        '텔루구어(인도)2', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('태국어(태국)', 'th-TH', 'th-TH-Neural2-C',
        '태국어(태국)1', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('태국어(태국)', 'th-TH', 'th-TH-Standard-A',
        '태국어(태국)2', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Standard-A',
        '터키어(터키)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Standard-B',
        '터키어(터키)2', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Standard-C',
        '터키어(터키)3', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Standard-D',
        '터키어(터키)4', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Standard-E',
        '터키어(터키)5', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Wavenet-A',
        '터키어(터키)6', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Wavenet-B',
        '터키어(터키)7', 'male', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Wavenet-C',
        '터키어(터키)8', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Wavenet-D',
        '터키어(터키)9', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('터키어(터키)', 'tr-TR', 'tr-TR-Wavenet-E',
        '터키어(터키)10', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('우크라이나어(우크라이나)', 'uk-UA', 'uk-UA-Standard-A',
        '우크라이나어(우크라이나)1', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('우크라이나어(우크라이나)', 'uk-UA', 'uk-UA-Wavenet-A',
        '우크라이나어(우크라이나)2', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Neural2-A',
        '베트남어(베트남)1', 'female', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Neural2-D',
        '베트남어(베트남)2', 'male', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Standard-A',
        '베트남어(베트남)3', 'female', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Standard-B',
        '베트남어(베트남)4', 'male', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Standard-C',
        '베트남어(베트남)5', 'female', '쓸쓸한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Standard-D',
        '베트남어(베트남)6', 'male', '분노하는', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Wavenet-A',
        '베트남어(베트남)7', 'female', '즐거운', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Wavenet-B',
        '베트남어(베트남)8', 'male', '차분한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Wavenet-C',
        '베트남어(베트남)9', 'female', '활발한', 1);


INSERT INTO voice_style (country, language_code, voice_type, voice_name, gender, personality, is_visible)
VALUES ('베트남어(베트남)', 'vi-VN', 'vi-VN-Wavenet-D',
        '베트남어(베트남)10', 'male', '쓸쓸한', 1);



