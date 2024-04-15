package com.hnv99.forum.service.user.service.help;

import java.util.Random;

/**
 * Username Generator
 */
public class UserRandomGenHelper {
    public static final String[] name_decorate = new String[]{
            "Mini", "Bright", "Swift", "Real", "Fresh", "Happy", "Cute", "Joyful", "Calm", "Intoxicated", "Casual", "Confused", "Positive", "Cold-blooded", "Passionate", "Rough",
            "Gentle", "Lovely", "Cheerful", "Loyal", "Serious", "Mighty", "Handsome", "Traditional", "Stylish", "Pretty", "Natural", "Devoted", "Obedient", "Drowsy", "Wild", "Waiting", "Playful",
            "Humorous", "Powerful", "Lively", "Happy", "Excited", "Super handsome", "Mustached", "Frank", "Direct", "Relaxed", "Devoted", "Perfect", "Astute", "Boring", "Charming", "Rich", "Prosperous",
            "Plump", "Fiery", "Irritable", "Blue", "Graceful", "Brave", "Forgetful", "Deliberate", "Careless", "Tyrannical", "Simple", "Excited", "Happy", "Calm", "Restless", "Generous", "Lonely",
            "Unique", "Crazy", "Fashionable", "Outdated", "Witty", "Sorrowful", "Bold", "Smiling", "Short", "Healthy", "Suitable", "Reckless", "Silent", "Cultivated", "Banana", "Apple", "Carp", "Eel",
            "Wayward", "Attentive", "Careless", "Careless", "Sweet", "Cool", "Strong", "Handsome", "Majestic", "Sunny", "Silent", "Powerful", "Filial", "Worried", "Anxious", "Nervous", "Kind",
            "Fierce", "Afraid", "Important", "Crisis", "Joyful", "Grateful", "Satisfied", "Jumping", "Sincere", "Satisfactory", "As desired", "Joyful", "Spoiled", "Helpless", "Speechless", "Excited", "Angry",
            "Beautiful", "Moved", "Passionate", "Exciting", "Thrilling", "Shaking", "Virtual", "Super", "Cold", "Astute", "Reasonable", "Hesitant", "Melancholy", "Lonely", "Struggling", "Diligent", "Modern", "Outdated",
            "Steady", "Passionate", "Implicit", "Open", "Innocent", "Passionate", "Pure", "Long", "Warm", "Focused", "Diligent", "Beautiful", "Shy", "Graceful", "Beautiful", "Sweet", "Charming", "Neat",
            "Moving", "Elegant", "Respectful", "Comfortable", "Graceful", "Enchanting", "Beautiful", "Delighted", "Sweet", "Sturdy", "Strong", "Generous", "Handsome", "Intelligent", "Charming", "Enchanting", "Enchanted",
            "Bright", "Solid", "Atmospheric", "Always late", "Intellectual", "Arrogant", "Silly", "Wild", "Invisible", "Low sense of humor", "Smiling", "Silly", "Sad", "Quiet", "On Mars", "Insomniac",
            "Quiet", "Pure", "Wanting to lose weight", "Lost", "Romantic", "Crying", "Virtuous", "Slender", "Gentle", "Coquettish", "Coquettish", "Playful", "Persistent", "Squinty", "Infatuated", "Wanting company",
            "Big-eyed", "Noble", "Proud", "Beautiful in soul", "Coquettish", "Delicate", "Innocent", "Afraid of the dark", "Sensitive", "Ethereal", "Afraid of loneliness", "Nervous", "Tall", "Silly", "Chilly", "Loving music",
            "Still single", "Afraid of loneliness", "Clueless"
    };
    public static final String[] name_body = new String[]{
            "Bubble Tea", "Shrimp", "Pikachu", "Mario", "Little Overlord", "Cold Noodles", "Lunch Box", "Edamame", "Peanuts", "Coke", "Light Bulb", "Hamigua", "Wild Wolf", "Backpack", "Eyes", "Fate", "Sprite", "Life", "Steak",
            "Ant", "Bird", "Gray Wolf", "Zebra", "Hamburger", "Wukong", "Giant", "Green Tea", "Bicycle", "Thermos", "Big Bowl", "Sunglasses", "Magic Mirror", "Pancake", "Mooncake", "Moon", "Stars", "Sesame", "Beer", "Rose",
            "Uncle", "Young Man", "Hamigua", "Data Cable", "Sun", "Leaves", "Celery", "Bee", "Powder", "Bee", "Envelope", "Suit", "Coat", "Dress", "Elephant", "Kitten", "Hen", "Street Lamp", "Blue Sky", "White Cloud",
            "Star Moon", "Rainbow", "Smile", "Motorcycle", "Chestnut", "High Mountain", "Earth", "Tree", "Light Bulb", "Brick", "Building", "Pool", "Chicken Wing", "Dragonfly", "Red Bull", "Coffee", "Doraemon", "Pillow", "Ship", "Promise",
            "Pen", "Hedgehog", "Sky", "Plane", "Cannon", "Winter", "Onion", "Spring", "Summer", "Autumn", "Winter", "Aviation", "Sweater", "Pea", "Black Rice", "Corn", "Eyes", "Mouse", "Aries", "Handsome Guy", "Beauty",
            "Season", "Flower", "Clothing", "Dress", "Plain Water", "Hair", "Mountain", "Train", "Car", "Song", "Dance", "Teacher", "Mentor", "Square Box", "Rice", "Oatmeal", "Water Cup", "Water Bottle", "Gloves", "Shoes", "Bicycle",
            "Mouse", "Mobile Phone", "Computer", "Textbook", "Miracle", "Figure", "Cigarette", "Sunset", "Desk Lamp", "Baby", "Future", "Belt", "Key", "Heart Lock", "Story", "Petals", "Skateboard", "Paintbrush", "Drawing Board", "Senior Sister", "Salesperson",
            "Power Supply", "Biscuit", "BMW", "Passerby", "White", "Time", "Stone", "Diamond", "Hippo", "Rhinoceros", "Buffalo", "Green Grass", "Drawer", "Cabinet", "Past", "Cold Wind", "Passerby", "Orange", "Headphones", "Ostrich", "Friend",
            "Slender", "Pencil", "Pen", "Coin", "Hot Dog", "Knight", "Elder Sister", "Loli", "Towel", "Expectation", "Hope", "Daytime", "Night", "Gate", "Black Pants", "Iron Man", "Dumbbell", "Stool", "Maple Leaf", "Lotus", "Turtle",
            "Cactus", "Shirt", "Morning", "Mood", "Jasmine", "Quicksand", "Snail", "Fighter Jet", "Pluto", "Cheetah", "Baseball", "Basketball", "Music", "Phone", "Network", "World", "Center", "Fish", "Chicken", "Dog",
            "Tiger", "Duck", "Rain", "Feather", "Wings", "Coat", "Fire", "Stockings", "Backpack", "Pen", "Cold Wind", "Eight Treasure Porridge", "Roast Chicken", "Wild Goose", "Sound", "Signboard", "Carrot", "Popsicle", "Hat", "Pineapple", "Egg Tart", "Perfume",
            "Kiwi", "Toasted Bread", "Stream", "Soybean", "Cherry", "Dove", "Butterfly", "Popcorn", "Roll", "Duckling", "Dolphin", "Diary", "Little Panda", "Lazy Pig", "Lazy Bug", "Lychee", "Mirror", "Cookie", "Enoki Mushroom",
            "Squirrel", "Shrimp", "Dew", "Jewelry", "Canvas Shoes", "Pitaya", "Kiwi", "Fried Egg", "Lipstick", "Potato", "High-heeled Shoes", "Ring", "Ice Cream", "Eyelashes", "Bell", "Bracelet", "Perfume", "Red Wine", "Moonlight", "Yogurt",
            "Tremella Soup", "Coffee Beans", "Little Bee", "Ant", "Candle", "Cotton Candy", "Sunflower", "Peach", "Butterfly", "Hedgehog", "Little Bun", "Nail Polish", "Carnation", "Candy Bean", "Potato Chips", "Lipstick", "Mini Skirt", "Udon", "Ice Cream",
            "Popsicle", "Giraffe", "Bean Sprouts", "Headband", "Hairpin", "Hair Clip", "Hair Band", "Bell", "Little Steamed Bun", "Xiaolongbao", "Little Cantaloupe", "Winter Melon", "Mushroom", "Strawberry", "Lemon", "Mooncake", "Lily", "Paper Crane", "Little Swan",
            "Cloud", "Mango", "Bread", "Swallow", "Kitten", "Totoro", "Lip Gloss", "Insole", "Sheep", "Black Cat", "White Cat", "Marlboro", "Golden Retriever", "Landscape", "Stereo", "Paper Plane", "Roast Goose"
    };

    private static final Random RANDOM = new Random();

    private static final int AVATAR_NUM = 92;

    private static final String AVATAR_TEMPLATE = "https://cdn.tobebetterjavaer.com/paicoding/avatar/%04d.png";

    /**
     * Automatically generates a nickname
     *
     * @return
     */
    public static String genNickName() {
        int decorateIndex = RANDOM.nextInt(name_decorate.length);
        int bodyIndex = RANDOM.nextInt(name_body.length);
        return name_decorate[decorateIndex] + " " + name_body[bodyIndex];
    }

    /**
     * Automatically selects an avatar
     *
     * @return
     */
    public static String genAvatar() {
        return String.format(AVATAR_TEMPLATE, RANDOM.nextInt(AVATAR_NUM) + 1);
    }

    /**
     * Generates a user invitation code
     * Rule: Prefix + YearMonthDay converted to hexadecimal
     *
     * @return
     */
    public static String genInviteCode(Long prefix) {
        return String.format("%03x%04x", prefix, System.currentTimeMillis() / 1000 / 60 / 60 / 24).toUpperCase();
    }
}
