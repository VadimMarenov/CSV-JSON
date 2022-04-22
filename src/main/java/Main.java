import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*__ 1. CSV - JSON парсер __*/
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString("data.json", json);

        /*__ 2. XML - JSON парсер __*/
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list);
        writeString("data2.json", json);
    }

    private static void writeString(String fileName, String text) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
            List<Employee> staff = null;
            return staff;
        }
    }

    private static List<Employee> parseXML(String fileName) {
        try {
            List<String> elements = new ArrayList<>();
            List<Employee> staff = new ArrayList<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeName().equals("employee")) {
                    NodeList nodeList1 = node.getChildNodes();
                    for (int j = 0; j < nodeList1.getLength(); j++) {
                        Node node_ = nodeList1.item(j);
                        if (Node.ELEMENT_NODE == node_.getNodeType()) {
                            elements.add(node_.getTextContent());
                        }
                    }
                    staff.add(new Employee(
                            Long.parseLong(elements.get(0)),
                            elements.get(1),
                            elements.get(2),
                            elements.get(3),
                            Integer.parseInt(elements.get(4))));
                    elements.clear();
                }
            }
            return staff;
        } catch (Exception e) {
            e.printStackTrace();
            List<Employee> staff = null;
            return staff;
        }
    }
}
