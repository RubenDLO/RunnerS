package com.example.runners;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FAQAdapter adapter;
    private List<FAQModel> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        recyclerView = findViewById(R.id.recyclerFaq);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        faqList = new ArrayList<>();
        fillFaqData();

        adapter = new FAQAdapter(faqList, this);
        recyclerView.setAdapter(adapter);
    }

    private void fillFaqData() {
        faqList.add(new FAQModel("¿Cómo debo hidratarme antes, durante y después de correr?",
                "Bebe 500-600 ml de agua 2-3 horas antes de correr y 150-250 ml 15 minutos antes. Durante carreras largas (más de 1 hora), consume 150-200 ml de agua o bebida isotónica cada 15-20 minutos. Después, rehidrátate con 500-1000 ml por cada kg de peso perdido, incluyendo electrolitos si sudaste mucho."));

        faqList.add(new FAQModel("¿Qué son las zonas de frecuencia cardíaca y cómo las uso?",
                "Las zonas de frecuencia cardíaca dividen tu esfuerzo en rangos (1 a 5) según tu frecuencia cardíaca máxima (FCM). Por ejemplo, la zona 2 (60-70% FCM) es ideal para entrenamientos de resistencia aeróbica. Usa un pulsómetro para entrenar en la zona adecuada según tu objetivo (quema de grasa, resistencia o velocidad)."));

        faqList.add(new FAQModel("¿Con qué frecuencia debo entrenar para mejorar como corredor?",
                "Los principiantes deben correr 3-4 veces por semana, combinando sesiones cortas y largas, con días de descanso o entrenamiento cruzado (como ciclismo o natación). Corredores avanzados pueden entrenar 5-6 días, incluyendo intervalos, rodajes largos y recuperación activa."));

        faqList.add(new FAQModel("¿Qué velocidad debo mantener al correr?",
                "Depende de tu nivel y objetivo. Los principiantes deben correr a un ritmo conversacional (puedes hablar sin jadear). Usa el ritmo de tus carreras fáciles como base y aumenta gradualmente en entrenamientos de velocidad o intervalos, según tu plan."));

        faqList.add(new FAQModel("¿Cómo evito lesiones al correr?",
                "Usa zapatillas adecuadas para tu tipo de pisada, aumenta el kilometraje no más del 10% por semana, incluye ejercicios de fuerza (core y piernas) y estira después de correr. Escucha a tu cuerpo y descansa si sientes dolor persistente."));

        faqList.add(new FAQModel("¿Qué debo comer antes de una carrera larga?",
                "Come un desayuno rico en carbohidratos (avena, pan integral, plátano) 2-3 horas antes, con algo de proteína y poca grasa. Evita alimentos pesados o nuevos. Durante carreras de más de 90 minutos, usa geles o barritas energéticas cada 45-60 minutos."));

        faqList.add(new FAQModel("¿Qué son los entrenamientos de intervalos y por qué son importantes?",
                "Los intervalos alternan esfuerzos intensos (como sprints) con descansos o trote suave. Mejoran la velocidad, la resistencia y la capacidad cardiovascular. Ejemplo: 6 repeticiones de 400 m a ritmo rápido con 1 minuto de recuperación."));

        faqList.add(new FAQModel("¿Cómo elijo las zapatillas adecuadas para correr?",
                "Haz un análisis de pisada (neutra, pronadora o supinadora) en una tienda especializada. Elige zapatillas según tu peso, tipo de entrenamiento (asfalto, trail) y amortiguación preferida. Cámbialas cada 600-800 km."));

        faqList.add(new FAQModel("¿Es mejor correr en ayunas o no?",
                "Correr en ayunas puede ayudar a quemar grasa, pero no es ideal para todos. Hazlo solo en sesiones cortas y fáciles (30-40 minutos) si te sientes bien. Para entrenamientos intensos, come algo ligero antes (como un plátano)."));

        faqList.add(new FAQModel("¿Cómo mejoro mi resistencia para correr distancias más largas?",
                "Aumenta gradualmente la duración de tus rodajes largos semanales, mantén un ritmo cómodo y combina con entrenamientos de fuerza y crosstraining. La consistencia y la paciencia son clave para construir resistencia."));

        faqList.add(new FAQModel("Consejos para Corredores",
                "Varía tus entrenamientos\n" +
                        "Combina rodajes largos, intervalos, cuestas y sesiones de recuperación para mejorar en todos los aspectos (velocidad, resistencia, fuerza) y evitar el estancamiento.\n\n" +
                        "Escucha tu cuerpo\n" +
                        "Si sientes fatiga extrema o dolor, reduce la intensidad o descansa. El sobreentrenamiento puede llevar a lesiones o agotamiento.\n\n" +
                        "Incorpora ejercicios de fuerza\n" +
                        "Haz sentadillas, zancadas, planchas y ejercicios de core 2-3 veces por semana para fortalecer músculos, mejorar la postura y prevenir lesiones.\n\n" +
                        "Mejora tu técnica de carrera\n" +
                        "Corre con pasos cortos y rápidos (180 pasos por minuto), aterriza con el mediopié y mantén el cuerpo ligeramente inclinado hacia adelante. Esto reduce el impacto y aumenta la eficiencia.\n\n" +
                        "Usa un plan de entrenamiento\n" +
                        "Sigue un plan adaptado a tu nivel y objetivos (5K, media maratón, etc.). Hay apps y entrenadores que pueden ayudarte a estructurar tus semanas.\n\n" +
                        "Hidrátate durante todo el día\n" +
                        "No esperes a estar sediento para beber agua. Mantén una hidratación constante, especialmente en días calurosos o de entrenamientos intensos.\n\n" +
                        "Prueba el entrenamiento cruzado\n" +
                        "Actividades como yoga, ciclismo o natación complementan el running, mejoran la flexibilidad y reducen el riesgo de lesiones por impacto repetitivo.\n\n" +
                        "Establece metas realistas\n" +
                        "Define objetivos alcanzables (correr 5K en 30 minutos, por ejemplo) y divídelos en metas semanales. Celebra los pequeños logros para mantener la motivación.\n\n" +
                        "Corre con amigos o en grupo\n" +
                        "Unirte a un club de running o entrenar con compañeros hace que las sesiones sean más divertidas y te motiva a ser constante.\n\n" +
                        "Registra tus progresos\n" +
                        "Usa un diario de entrenamiento o apps como Strava para anotar tus tiempos, distancias y sensaciones. Analizar tu progreso te ayuda a ajustar tu plan y mantenerte motivado."));

    }
}
