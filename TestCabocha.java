package sjjTEST;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.List;


public class TestCabocha {
    public static void main(String[] args) {
        TestCabocha test1 = new TestCabocha();
        String savePathString = "D:/Cabocha.csv";
        String contentString = "私たちは、新しい流通のステージに向けて、1日1日少しずつ、地道に確実に、当たり前のことを当たり前に行ってまいります。かけがえのない生涯を、夢ある人生をかけるにふさわしいロマンに向かって、トライする仲間がいる会社が、私たちトライアルカンパニーです。";
        List<CabochaVO> listCabocha = test1.Run(contentString);
        test1.writeCSV(savePathString, listCabocha);
    }

    public List<CabochaVO> Run(String text) {
        List mylistCabocha = new ArrayList<CabochaVO>();

        try {
            //UTF-8のBOMを除去するための準備←textファイルから読み込む場合を考慮
            //            byte[] bytes = { -17, -69, -65 };
            byte[] bytes = {  };
            String btmp = new String(bytes, "UTF-8");
            //BOM除去
            text = text.replaceAll(btmp, "");

            //cabochaの実行開始　ラティス形式で出力(-f1の部分で決定、詳しくはcabochaのhelp参照)
            ProcessBuilder pb = new ProcessBuilder("cabocha", "-f1");
            Process process = pb.start();

            //実行途中で文字列を入力(コマンドプロンプトで文字を入力する操作)
            OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream(),
                    "UTF-8");
            osw.write(text);
            osw.close();

            //出力結果を読み込む
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));

            //一行ずつ読み込むための文字列の変数を用意
            String Line = "";

            //出力結果の各行毎に配列へ格納するためのリストを用意
            ArrayList out = new ArrayList();

            //最後の行までやり続ける
            while ((Line = br.readLine()) != null) {
                //読み込んだ行をリストへ格納
                out.add(Line);

                //行をコンソールへ表示
                if (!Line.startsWith("*") && !Line.startsWith("EOS")) {
                    String[] arrayLineString = Line.split("\t");
                    String keyWord = arrayLineString[0];
                    String originalForm = arrayLineString[1].split(",")[6];
                    String property = arrayLineString[1].split(",")[0];
                    String detail = arrayLineString[1].split(",")[1];
                    CabochaVO v1 = new CabochaVO();
                    v1.setKeyWord(keyWord);
                    v1.setOriginalForm(originalForm);
                    v1.setProperty(property);
                    v1.setDetail(detail);
                    mylistCabocha.add(v1);
                }
            }

            //プロセス終了
            process.destroy();
            process.waitFor();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mylistCabocha;
    }

    public void writeCSV(String path, List<CabochaVO> paralistCabocha) {
        String csvFilePath = path;

        try {
            // 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
            CsvWriter csvWriter = new CsvWriter(csvFilePath, ',',
                    Charset.forName("UTF-8"));

            // 写内容
            String[] headers = { "キーワード", "原形", "品詞", "品詞細分類" };
            csvWriter.writeRecord(headers);

            for (int i = 0; i < paralistCabocha.size(); i++) {
                CabochaVO v2 = paralistCabocha.get(i);
                String[] writeLine = {
                        v2.getKeyWord(), v2.getOriginalForm(), v2.getProperty(),
                        v2.getDetail()
                    };
                csvWriter.writeRecord(writeLine);
            }

            csvWriter.close();
            System.out.println("--------CSV文件已经写入--------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
