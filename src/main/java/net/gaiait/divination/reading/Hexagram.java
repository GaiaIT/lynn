package net.gaiait.divination.reading;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Hexagram {
    FORCE(1, "Force", "乾", "䷀"),
    FIELD(2, "Field", "坤", "䷁"),
    SPROUTING(3, "Sprouting", "屯", "䷂"),
    ENVELOPING(4, "Enveloping", "蒙", "䷃"),
    ATTENDING(5, "Attending", "需", "䷄"),
    ARGUING(6, "Arguing", "訟", "䷅"),
    LEADING(7, "Leading", "師", "䷆"),
    GROUPING(8, "Grouping", "比", "䷇"),
    SMALL_ACCUMULATING(9, "Small Accumulating", "小畜 ", "䷈"),
    TREADING(10, "Treading", "履", "䷉"),
    PERVADING(11, "Pervading", "泰", "䷊"),
    OBSTRUCTION(12, "Obstruction", "否", "䷋"),
    CONCORDING_PEOPLE(13, "Concording People", "同人", "䷌"),
    GREAT_POSSESSING(14, "Great Possessing", "大有", "䷍"),
    HUMBLING(15, "Humbling", "謙", "䷎"),
    PROVIDING_FOR(16, "Providing-For", "豫", "䷏"),
    FOLLOWING(17, "Following", "隨", "䷐"),
    CORRECTING(18, "Correcting", "蠱", "䷑"),
    NEARING(19, "Nearing", "臨", "䷒"),
    VIEWING(20, "Viewing", "觀", "䷓"),
    GNAWING_BITE(21, "Gnawing Bite", "噬嗑", "䷔"),
    ADORNING(22, "Adorning", "賁", "䷕"),
    STRIPPING(23, "Stripping", "剝", "䷖"),
    RETURNING(24, "Returning", "復", "䷗"),
    WITHOUT_EMBROILING(25, "Without Embroiling", "無妄", "䷘"),
    GREAT_ACCUMULATING(26, "Great Accumulating", "大畜", "䷙"),
    SWALLOWING(27, "Swallowing", "頤 ", "䷚"),
    GREAT_EXCEEDING(28, "Great Exceeding", "大過", "䷛"),
    GORGE(29, "Gorge", "坎", "䷜"),
    RADIANCE(30, "Radiance", "離", "䷝"),
    CONJOINING(31, "Conjoining", "咸", "䷞"),
    PERSEVERING(32, "Persevering", "恆", "䷟"),
    RETIRING(33, "Retiring", "遯", "䷠"),
    GREAT_INVIGORATING(34, "Great Invigorating", "大壯", "䷡"),
    PROSPERING(35, "Prospering", "晉", "䷢"),
    DARKENING_OF_THE_LIGHT(36, "Darkening of the Light", "明夷", "䷣"),
    DWELLING_PEOPLE(37, "Dwelling People", "家人", "䷤"),
    POLARISING(38, "Polarising", "睽", "䷥"),
    LIMPING(39, "Limping", "蹇", "䷦"),
    TAKING_APART(40, "Taking-Apart", "解", "䷧"),
    DIMINISHING(41, "Diminishing", "損", "䷨"),
    AUGMENTING(42, "Augmenting", "損", "䷩"),
    DISPLACEMENT(43, "Displacement", "夬", "䷪"),
    COUPLING(44, "Coupling", "姤", "䷫"),
    CLUSTERING(45, "Clustering", "萃", "䷬"),
    ASCENDING(46, "Ascending", "升", "䷭"),
    CONFINING(47, "Confining", "困", "䷮"),
    WELLING(48, "Welling", "井", "䷯"),
    SKINNING(49, "Skinning", "革", "䷰"),
    HOLDING(50, "Holding", "鼎", "䷱"),
    SHAKE(51, "Shake", "震", "䷲"),
    BOUND(52, "Bound", "艮", "䷳"),
    INFILTRATING(53, "Infiltrating", "漸", "䷴"),
    CONVERTING_THE_MAIDEN(54, "Converting the Maiden", "歸妹", "䷵"),
    ABOUNDING(55, "Abounding", "豐", "䷶"),
    SOJOURNING(56, "Sojourning", "旅", "䷷"),
    GROUND(57, "Ground", "巽", "䷸"),
    OPEN(58, "Open", "兌", "䷹"),
    DISPERSING(59, "Dispersing", "渙", "䷺"),
    ARTICULATING(60, "Articulating", "節", "䷻"),
    CENTER_RETURNING(61, "Center Returning", "中孚", "䷼"),
    SMALL_EXCEEDING(62, "Small Exceeding", "小過", "䷽"),
    ALREADY_FORDING(63, "Already Fording", "既濟", "䷾"),
    NOT_YET_FORDING(64, "Not Yet Fording", "未濟", "䷿");

    private static final Logger LOGGER = LogManager.getLogger(Hexagram.class);

    public static final String NUMBER_FIELD = "number";
    public static final String ENGLISH_FIELD = "english";
    public static final String CHINESE_FIELD = "chinese";
    public static final String SYMBOL_FIELD = "symbol";

    private final int number;
    private final String english;
    private final String chinese;
    private final String symbol;
    private final transient Map<String, Object> json;

    Hexagram(int number, String english, String chinese, String symbol) {
        this.number = number;
        this.english = english;
        this.chinese = chinese;
        this.symbol = symbol;

        Map<String, Object> hexJson = new HashMap<>();
        hexJson.put(NUMBER_FIELD, number);
        hexJson.put(ENGLISH_FIELD, english);
        hexJson.put(CHINESE_FIELD, chinese);
        hexJson.put(SYMBOL_FIELD, symbol);
        this.json = hexJson;
    }

    public int getNumber() {
        return number;
    }

    public String getEnglish() {
        return english;
    }

    public String getChinese() {
        return chinese;
    }

    public String getSymbol() {
        return symbol;
    }

    public Map<String, Object> getJson() {
        return Collections.unmodifiableMap(json);
    }

    public static Hexagram ofInt(int hexagramNo) {
        if (!validateHexagramNo(hexagramNo)) {
            throw new IllegalArgumentException();
        }
        return Hexagram.values()[hexagramNo - 1];
    }
    
    public static boolean validateHexagramNo(int hexagramNo) {
        if (hexagramNo < 1 || hexagramNo > 64) {
            LOGGER.error("Hexagram value {} is not within valid range", hexagramNo);
            return false;
        }
        return true;
    }

}
