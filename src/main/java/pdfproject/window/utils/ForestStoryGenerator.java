package pdfproject.window.utils;

import org.apache.poi.xwpf.usermodel.*;
import java.io.FileOutputStream;
import java.io.IOException;

public class ForestStoryGenerator {

    public static void main(String[] args) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        // Helper to insert page breaks
        Runnable pageBreak = () -> {
            XWPFParagraph p = doc.createParagraph();
            p.setPageBreak(true);
        };

        // --- Page 1: Title and Characters ---
        addTitle(doc, "🌳 Whispering Woods and the Secret Map");
        addSubtitle(doc, "A Magical Tale of Friendship, Curiosity, and Courage");

        addParagraph(doc,
                "In the heart of a forest where trees whispered stories and fireflies lit up the night, " +
                "lived a group of curious animal friends. One fine morning, Leo the Lion stumbled upon a mysterious map " +
                "tucked inside a hollow log near his den.");

        addHeading(doc, "🐾 Meet the Explorers");
        addBulletedList(doc, new String[]{
                "Leo the Lion 🦁 – Brave and full of questions",
                "Ellie the Elephant 🐘 – Wise and kind",
                "Milo the Monkey 🐒 – Quick and always joking",
                "Tina the Tortoise 🐢 – Slow but very clever",
                "Ollie the Owl 🦉 – The nighttime guide"
        });

        addSubheading(doc, "The Mission Begins");
        addParagraph(doc,
                "The map showed a trail of riddles leading to something called “The Heart of the Forest.” " +
                "The animals had never heard of it before. Their tails twitched and ears perked up — an adventure had begun!");

        pageBreak.run();

        // --- Page 2: Clues and Challenges ---
        addHeading(doc, "🌲 Deep into the Forest");
        addParagraph(doc,
                "The journey began at the riverbank. Ellie helped everyone cross the stream, lifting Leo and Tina on her strong back. " +
                "Meanwhile, Milo swung from trees, keeping spirits high.");

        addSubheading(doc, "The Riddle Tree");
        addParagraph(doc,
                "A glowing tree whispered:\n\n" +
                "“I have no mouth, yet I tell stories. I have no legs, but I travel far. What am I?”\n\n" +
                "The friends thought. Ollie blinked. “A book!” he hooted. A golden leaf floated down as the path lit up again.");

        addSubheading(doc, "The Sleepy Shadows");
        addParagraph(doc,
                "As dusk fell, the forest grew quiet. Mysterious shapes moved in the mist. Tina reminded everyone to stay close. " +
                "A warm firefly glow surrounded them, and a lullaby sung by Ollie helped them rest under a giant leaf roof.");

        pageBreak.run();

        // --- Page 3: The Heart of the Forest ---
        addHeading(doc, "✨ The Glowing Clearing");
        addParagraph(doc,
                "As the moon rose, the animals stepped into a clearing bathed in silver light. " +
                "A circle of ancient stones shimmered, and in the center sat a crystal flower. The map fluttered and vanished.");

        addSubheading(doc, "The Final Discovery");
        addParagraph(doc,
                "The crystal opened to reveal a message:\n\n" +
                "“The treasure is not gold, but your friendship, courage, and the fun you shared.”");

        addParagraph(doc,
                "The animals cheered and danced. Fireflies spun above them like stars. They laughed, hugged, and promised to return " +
                "to Whispering Woods for more adventures.");

        addFinalLine(doc, "🐾 The End... Or is it just the beginning?");

        try (FileOutputStream out = new FileOutputStream("ForestAnimalStory.docx")) {
            doc.write(out);
            System.out.println("Story created: ForestAnimalStory.docx");
        }
    }

    private static void addTitle(XWPFDocument doc, String text) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText(text);
        run.setFontSize(28);
        run.setBold(true);
        run.setColor("228B22"); // Forest green
    }

    private static void addSubtitle(XWPFDocument doc, String text) {
        XWPFParagraph subtitle = doc.createParagraph();
        subtitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = subtitle.createRun();
        run.setText(text);
        run.setFontSize(16);
        run.setItalic(true);
        run.setColor("666666");
    }

    private static void addHeading(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(18);
        run.setColor("006400"); // Dark green
    }

    private static void addSubheading(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(14);
        run.setColor("8B4513"); // Brown
    }

    private static void addParagraph(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(150);
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setFontSize(12);
    }

    private static void addBulletedList(XWPFDocument doc, String[] items) {
        for (String item : items) {
            XWPFParagraph bullet = doc.createParagraph();
            bullet.setStyle("ListBullet");
            XWPFRun run = bullet.createRun();
            run.setText(item);
            run.setFontSize(12);
        }
    }

    private static void addFinalLine(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setFontSize(20);
        run.setBold(true);
        run.setColor("FF4500"); // Orange red
    }
}
