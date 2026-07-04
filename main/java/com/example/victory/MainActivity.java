package com.example.victory;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tvQuestion;
    RadioGroup radioGroup;
    RadioButton rb1, rb2, rb3, rb4;
    Button btnNext;

    List<Question> questions = new ArrayList<>();
    int currentIndex = 0;
    int correctAnswers = 0;

    class Question {
        String questionText;
        String[] answers = new String[4];
        int correctIndex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим виджеты по ид
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        btnNext = findViewById(R.id.btnNext);

        // Загружаем вопросы из JSON файла
        loadQuestionsFromJson();

        // Загружаем первый вопрос
        loadQuestion();

        btnNext.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Выберите ответ", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIndex = -1;
            if (selectedId == R.id.rb1) selectedIndex = 0;
            if (selectedId == R.id.rb2) selectedIndex = 1;
            if (selectedId == R.id.rb3) selectedIndex = 2;
            if (selectedId == R.id.rb4) selectedIndex = 3;

            // Проверяем ответ
            if (selectedIndex == questions.get(currentIndex).correctIndex) {
                correctAnswers++;
            }

            currentIndex++;
            if (currentIndex < questions.size()) {
                loadQuestion();
            } else {
                showResult();
            }
        });
    }

    private void loadQuestionsFromJson() {
        String jsonStr = loadJSONFromRaw();
        try {
            JSONObject root = new JSONObject(jsonStr);
            JSONArray arr = root.getJSONArray("questions");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Question q = new Question();

                q.questionText = obj.getString("questionText");

                JSONArray ansArr = obj.getJSONArray("options");
                for (int j = 0; j < 4; j++) {
                    q.answers[j] = ansArr.getString(j);
                }

                // ищем правильный ответ
                String correct = obj.getString("correctAnswer");
                for (int j = 0; j < 4; j++) {
                    if (q.answers[j].equals(correct)) {
                        q.correctIndex = j;
                        break;
                    }
                }

                questions.add(q);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String loadJSONFromRaw() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = getResources().openRawResource(R.raw.questions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void loadQuestion() {
        if (currentIndex >= 0 && currentIndex < questions.size()) {
            Question q = questions.get(currentIndex);
            tvQuestion.setText(q.questionText);
            rb1.setText(q.answers[0]);
            rb2.setText(q.answers[1]);
            rb3.setText(q.answers[2]);
            rb4.setText(q.answers[3]);
            radioGroup.clearCheck();
        }
    }
    private void showResult() {
        tvQuestion.setText("Вы ответили правильно на " + correctAnswers + " из " + questions.size() + " вопросов");
        radioGroup.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
    }
}