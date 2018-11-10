import java.io.IOException;
import java.net.URL;

import net.semanticmetadata.lire.sampleapp.Indexer;


public class test {
    public static void main(String[] args) throws IOException {
        Indexer ind = new Indexer();
        ind.indexImageFromURL(new URL("https://bit.ua/wp-content/uploads/2017/08/DH8q-hWVwAAWkzo-min.jpg"));

        Indexer ind2 = new Indexer();
        ind2.indexImageFromURL(new URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXUogMbmDFjebdKgUx6vIffippvgUc3n-cIlho-qbI2Px-i2MHoA"));

        Indexer ind3 = new Indexer();
        ind3.indexImageFromURL(new URL("https://media.thequestion.ru/cards/question/306088/a05a296dfd3f18dbc306555b63272a36ee879ce4?t=1503817581"));


        ind.compaire(ind2);
        ind.compaire(ind3);
        ind2.compaire(ind3);
    }
}
