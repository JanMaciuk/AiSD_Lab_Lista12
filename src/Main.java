import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {
    private static final String textFilename = "tekst.txt";
    private static final String codeFileName = "kod.txt";
    public static void main(String[] args) {
        ArrayList<Node> nodes = nodesFromFile();
        Node rootNode = buildHuffmanTree(nodes);
        String text = getFileText(textFilename);

        System.out.println("Oryginalny tekst: "+text);
        printAllCodes(rootNode,"");

        String code = getTextCodes(rootNode,text);
        writeCodeToFile(code);
        System.out.println("Zakodowany tekst: " + code);
        System.out.println("Odkodowany tekst: " + decodeText(rootNode,getFileText(codeFileName)));
    }

    public static String decodeText(Node root,String code){
        // Odkodowuje tekst przechodząc po drzewie Huffmana.
        StringBuilder decodedText = new StringBuilder();
        Node node = root;
        for (Character ch: code.toCharArray()) {
            //Jeśli 1 to w prawo, jeśli 0 to w lewo
            if (ch == '1') node = node.right;
            else node = node.left;
            // Jeżeli węzeł nie ma dzieci to jest liściem, czyli zawiera znak.
            // Zwracam znak i wracam do korzenia aby zacząć szukanie nowego znaku.
            if (node.right == null && node.left == null) {
                decodedText.append(node.symbol);
                node = root;
            }
        }

        return decodedText.toString();
    }


    public static String getTextCodes(Node root, String text) {
        // Dla każdego znaku string-a szukam jego kodu i dodaje do całkowitego kodu
        StringBuilder string = new StringBuilder();
        for (Character ch:text.toCharArray()) {
            // Dla każdego znaku w stringu znajdź jego kod
            string.append(getCode(root,ch,""));
        }

        // Pierwszym elementem tablicy kodów będzie całkowity kod (do czystszego wyświetlania bez pętli)
        return string.toString();
    }

    public static Node buildHuffmanTree(ArrayList<Node> nodes) {
        // Buduje drzewo Huffmana zgodnie z algorytmem z zadania.
        PriorityQueue<Node> queue = new PriorityQueue<>(nodes.size(), new nodeFrequencyComparator());
        queue.addAll(nodes);
        /*
        Algorytm:
        1. Utwórz zbiór drzew z korzeniami zawierającymi poszczególne znaki i ich wagi.
        2. Umieść zbiór drzew w kolejce priorytetowej.
        3. while kolejka priorytetowa zawiera więcej niż jeden element
        a. usuń dwa drzewa o najmniejszych wagach
        b. połącz je w jedno drzewo binarne, w którym waga korzenia jest sumą wag jego
        dzieci
        c. wstaw utworzone w ten sposób drzewo z powrotem do kolejki priorytetowej
         */
        while (queue.size() > 1) {
            Node lNode = queue.poll();
            Node rNode = queue.poll();
            Node parentNode = new Node(lNode.frequency+ rNode.frequency,null);
            parentNode.left = lNode;
            parentNode.right = rNode;
            queue.add(parentNode);
        }


        return queue.poll();
    }


    public static void printAllCodes(Node node, String kod){
        // Przegląd drzewa, po drodze generując kod wszystkich elementów.

        // Jeżeli węzeł nie ma dzieci to jest liściem, czyli zawiera znak, wyświetlam węzeł
        if (node.left == null && node.right == null) {

            if (node.symbol != ' ') {
                System.out.println(node.symbol + "      - " + node.frequency + "   "+ kod);
            }
            else { // Jeżeli char to spacja to wyświetlam napis spacja zamiast pustego pola
                System.out.println("spacja" + " - " + node.frequency + "   "+ kod);
            }
            return;
        }

        // Generowanie kodu Huffmana, przechodząc w lewo dodaje "0" do kodu, w prawo dodaję "1"
        if (node.left != null)  printAllCodes(node.left, kod + "0");
        if (node.right != null) printAllCodes(node.right, kod + "1");
    }


    public static String getCode( Node node,Character searched, String kod) {
        // Znajdź kod dopowiadający podanemu symbolowi.
        /*
        Jeżeli węzeł nie ma dzieci to jest liściem, czyli zawiera znak.
        Sprawdzam czy jest to szukany znak, jeśli tak, zwracam jego kod.
         */
        if (node.left == null && node.right == null && node.symbol == searched) { return kod; }

        // Generowanie kodu Huffmana, przechodząc w lewo dodaje "0" do kodu, w prawo dodaję "1"
        String kod1 = null,kod2 = null;
        if (node.left != null)  kod1 = getCode(node.left,searched, kod + "0");
        if (node.right != null) kod2 = getCode(node.right,searched, kod + "1");
        // Jeżeli znalazłem szukany element w poddrzewie, to jego wartość zwrotna nie będzie null-em.
        if (kod1!=null) return kod1;
        else return kod2;
    }

    public static ArrayList<Node> nodesFromFile() {
        // Odczytuje plik o podanej nazwie linia po linii, zwraca listę unikalnych węzłów zawierających wszystkie znaki.
        ArrayList<Node> nodes = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(textFilename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Character ch:line.toCharArray()) {
                    if(!nodes.contains(new Node(0, ch))) {
                        nodes.add(new Node(1, ch));
                    }
                    else {nodes.get(nodes.indexOf(new Node(1, ch))).frequency++;}

                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }

    public static String getFileText(String filename) {
        // Po prostu odczytuję i zwracam tekst z pliku.
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while((line = reader.readLine())!=null)
            {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void writeCodeToFile(String tekst) {
        // Zapisuje kod do pliku.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(codeFileName))) {
            writer.write(tekst);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class nodeFrequencyComparator implements Comparator<Node>{
    public int compare(Node arg0, Node arg1){
        if(arg0.symbol != null){
            if(arg1.symbol != null){
                // Jeżeli obydwa węzły mają wartości to normalnie porównuje po częstotliwości.
                return Integer.compare(arg0.frequency, arg1.frequency);
            }
            //Jeżeli węzeł 0 ma wartość, a 1 jest null-em, to priorytetyzuje węzeł z wartością.
            return -1;
        }
        if(arg1.symbol != null){
            //Jeżeli węzeł 1 ma wartość, a 0 jest null-em, to priorytetyzuje węzeł z wartością.
            return 1;
        }
        return 0; // Jeżeli obydwa węzły nie mają znaku to są równe.
    }
}