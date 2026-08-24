package com.example.data.staticdata

data class HadithVirtueItem(
    val id: String,
    val title: String,
    val practiceTiming: String,
    val narrationText: String,
    val virtueBenefit: String
)

object HadithData {
    val items: List<HadithVirtueItem> = listOf(
        HadithVirtueItem(
            id = "hadith_imam_hasan_salawat",
            title = "حديث الإمام الحسن (ع) في الصلاة على النبي وذريته",
            practiceTiming = "بعد صلاة الصبح وصلاة المغرب — قبل أن يثني رجليه أو يكلم أحداً",
            narrationText = "الحسن (ع) يقول: من قال في دبر صلاة الصبح و صلاة المغرب قبل أن يثني رجليه أو يكلم أحداً إن الله و ملائكته يصلون على النبي يا أيها الذين آمنوا صلوا عليه و سلموا تسليماً، اللهم صل على محمد و ذريته، قضى الله له مائة حاجة سبعين في الدنيا و ثلاثين في الآخرة.",
            virtueBenefit = "قضاء مائة حاجة: سبعون منها في الدنيا وثلاثون في الآخرة."
        ),
        HadithVirtueItem(
            id = "virtue_kahf_10",
            title = "فضل حفظ أول ١٠ آيات من سورة الكهف",
            practiceTiming = "قراءة وحفظ وتدبر مستمر",
            narrationText = "من حفظ عشر آيات من أول سورة الكهف عُصم من فتنة الدجال، وكانت له نوراً ووقاية وأمناً وهداية إلى يوم القيامة.",
            virtueBenefit = "العصمة من الفتن والأمان والنور التام."
        ),
        HadithVirtueItem(
            id = "virtue_ayat_kursi",
            title = "فضل قراءة آية الكرسي بعد كل صلاة",
            practiceTiming = "دبر كل صلاة مكتوبة وعند الاستيقاظ وعند النوم",
            narrationText = "من قرأ آية الكرسي في دبر كل صلاة مكتوبة لم يمنعه من دخول الجنة إلا الموت، وتولى الله قبض روحه بيده، وكان كمن جاهد مع الأنبياء.",
            virtueBenefit = "حفظ الله التام، بركة في الرزق، ودخول الجنة."
        ),
        HadithVirtueItem(
            id = "virtue_ikhlas_10",
            title = "فضل قراءة سورة الإخلاص ١٠ مرات",
            practiceTiming = "خلال اليوم وقبل النوم",
            narrationText = "من قرأ قل هو الله أحد عشر مرات بنى الله له قصراً في الجنة، ومن قرأها عشرين مرة بنى الله له قصرين، ومن قرأها ثلاثين مرة بنى الله له ثلاثة قصور.",
            virtueBenefit = "بناء القصور في الجنة ومغفرة الذنوب."
        ),
        HadithVirtueItem(
            id = "virtue_hawqala",
            title = "فضل لا حول ولا قوة إلا بالله",
            practiceTiming = "وقت الغروب وخلال اليوم",
            narrationText = "لا حول ولا قوة إلا بالله كنز من كنوز الجنة، وباب من أبوابها، وشفاء من تسعة وتسعين داء أيسرها الهم والحزن.",
            virtueBenefit = "كنز من كنوز الجنة ودفع الهموم والغموم."
        ),
        HadithVirtueItem(
            id = "virtue_1000_tahlil",
            title = "فضل ١٠٠٠ مرة لا إله إلا الله",
            practiceTiming = "في الأيام والليالي المباركة وسائر الأوقات",
            narrationText = "أفضل الذكر لا إله إلا الله، ومن قالها مخلصاً ثقل بها ميزانه يوم القيامة وغُفرت له ذنوبه ورُفعت له الدرجات العلى.",
            virtueBenefit = "تثقيل الموازين، تجديد الإيمان، ومحو السيئات."
        ),
        HadithVirtueItem(
            id = "virtue_1000_istighfar",
            title = "فضل ١٠٠٠ مرة أستغفر الله",
            practiceTiming = "وقت السحر وخلال اليوم",
            narrationText = "من لزم الاستغفار جعل الله له من كل هم فرجاً، ومن كل ضيق مخرجاً، ورزقه من حيث لا يحتسب.",
            virtueBenefit = "تفريج الهموم، سعة الأرزاق، وتيسير الأمور."
        ),
        HadithVirtueItem(
            id = "virtue_shahada",
            title = "فضل الشهادة والتوحيد",
            practiceTiming = "صباحاً ومساءً",
            narrationText = "شهادة أن لا إله إلا الله وأن محمداً رسول الله ﷺ هي الكلمة الطيبة وحصن الله الحصين الذي من دخله أمِن من عذاب الله.",
            virtueBenefit = "الأمان من العذاب والنجاة في الدارين."
        )
    )
}
