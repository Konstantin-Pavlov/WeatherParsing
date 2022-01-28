import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.URL;

public class Parser {
    private static Document getPage() throws IOException {
        String url = "http://pogoda.spb.ru/";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    //  может переписать этот метод его
    private static int getIterationsNumber(Elements values, int index) {
        int iterationsNumber = 4;

        if (index != 0) {
            return iterationsNumber;
        }

        //  может быть до 5 строк
        //  по значению, получаем с чего начинается - либо с утро, либо с день, либо с ночь (тогда 5 строк, объединение с сл днем)
        /*В вечернее время может быть 5 строк, то есть ночь объединяется со следующим днем, получается что-то такое:
        Ночь Пасмурно. (95%) Без осадков. -10..-12 751 98% [С-З] 1-3 м/с
        Утро Облачно. (84%) Без осадков. -16..-18 752 100% [З] 1-3 м/с
        День Облачно. (75%) Без осадков. -6..-8 750 96% [Ю] 1-3 м/с
        Вечер Пасмурно. (100%) Без осадков. -4..-6 743 87% [Ю] 4-6 м/с
        Ночь Пасмурно. (100%) Местами небольшой снег. (0.4 мм.) -4..-6 734 89% [Ю-В] 7-9 м/с
        Следующие дни уже идут по 4 строки*/

        Element valuesLine = values.get(0);
        boolean isMorning = valuesLine.text().contains("Утро");
        boolean isDayTime = valuesLine.text().contains("День");
        boolean isEvening = valuesLine.text().contains("Вечер");
        boolean isNight = valuesLine.text().contains("Ночь");
        if (isMorning) {
        }
        if (isDayTime) {
            iterationsNumber--;
        }
        if (isEvening) {
            iterationsNumber--;
        }
        if (isNight) { // почему-то это условие всегда выполняется - выяснить почему?
            iterationsNumber = 5;
        }
        return iterationsNumber;
    }

    private static void printFourValues(Elements values, int index) {
        int iterationsCount = 4;

        if (index == 0) {
            iterationsCount = getIterationsNumber(values, index);
        }
        for (int i = 0; i < iterationsCount; i++) {
            Element valuesLine = values.get(index + i);
            for (Element td : valuesLine.select("td")) {
                System.out.print(td.text() + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(getPage());
        Document page = getPage();
        Element tableWeather = page.select("table[class=wt]").first();
        Elements names = tableWeather.select("tr[class=wth]");
        Elements values = tableWeather.select("tr[valign=top]");
        int index = 0;
        for (Element name : names) {
            String[] date = name.select("th[id=dt]").text().split(" ");
            System.out.println(date[0] + "\tweather conditions\ttemperature\tpressure\thumidity\twind");
            printFourValues(values, index);
            index += getIterationsNumber(values, index);
            System.out.println();
        }


//        final String date = names.select("th[id=dt]").text().split("\\w+", 1)[0];
//        System.out.println(date);
//        System.out.println(date + "weather conditions\ttemperature\tpressure\thumidity\twind");

    }
}
