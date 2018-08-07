package org.telegram.plugins.xuser.support;

import java.util.Random;

public class CreateRandomField {

	public static final String[] FEMALE_FIRST_NAMES = {

	"Mary", "Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer", "Maria",
			"Susan",

			"Margaret", "Dorothy", "Lisa", "Nancy", "Karen", "Betty", "Helen",
			"Sandra", "Donna",

			"Carol", "Ruth", "Sharon", "Michelle", "Laura", "Sarah",
			"Kimberly", "Deborah", "Jessica",

			"Shirley", "Cynthia", "Angela", "Melissa", "Brenda", "Amy", "Anna",
			"Rebecca", "Virginia",

			"Kathleen", "Pamela", "Martha", "Debra", "Amanda", "Stephanie",
			"Carolyn", "Christine",

			"Marie", "Janet", "Catherine", "Frances", "Ann", "Joyce", "Diane",
			"Alice", "Julie",

			"Heather", "Teresa", "Doris", "Gloria", "Evelyn", "Jean", "Cheryl",
			"Mildred", "Katherine",

			"Joan", "Ashley", "Judith", "Rose", "Janice", "Kelly", "Nicole",
			"Judy", "Christina",

			"Kathy", "Theresa", "Beverly", "Denise", "Tammy", "Irene", "Jane",
			"Lori", "Rachel",

			"Marilyn", "Andrea", "Kathryn", "Louise", "Sara", "Anne",
			"Jacqueline", "Wanda", "Bonnie",

			"Julia", "Ruby", "Lois", "Tina", "Phyllis", "Norma", "Paula",
			"Diana", "Annie", "Lillian",

			"Emily", "Robin", "Peggy", "Crystal", "Gladys", "Rita", "Dawn",
			"Connie", "Florence",

			"Tracy", "Edna", "Tiffany", "Carmen", "Rosa", "Cindy", "Grace",
			"Wendy", "Victoria", "Edith",

			"Kim", "Sherry", "Sylvia", "Josephine", "Thelma", "Shannon",
			"Sheila", "Ethel", "Ellen",

			"Elaine", "Marjorie", "Carrie", "Charlotte", "Monica", "Esther",
			"Pauline", "Emma",

			"Juanita", "Anita", "Rhonda", "Hazel", "Amber", "Eva", "Debbie",
			"April", "Leslie", "Clara",

			"Lucille", "Jamie", "Joanne", "Eleanor", "Valerie", "Danielle",
			"Megan", "Alicia", "Suzanne",

			"Michele", "Gail", "Bertha", "Darlene", "Veronica", "Jill", "Erin",
			"Geraldine", "Lauren",

			"Cathy", "Joann", "Lorraine", "Lynn", "Sally", "Regina", "Erica",
			"Beatrice", "Dolores",

			"Bernice", "Audrey", "Yvonne", "Annette", "June", "Samantha",
			"Marion", "Dana", "Stacy",

			"Ana", "Renee", "Ida", "Vivian", "Roberta", "Holly", "Brittany",
			"Melanie", "Loretta",

			"Yolanda", "Jeanette", "Laurie", "Katie", "Kristen", "Vanessa",
			"Alma", "Sue", "Elsie", "娴", "巧", "秀", "娟", "英", "华", "美", "芸",
			"娜", "静", "珠", "婷", "雅", "淑", "薇", "芝", "玉", "娅", "玲", "芬", "芳",
			"梦", "彩", "佳", "妍", "琼", "艺", "柔", "卿", "聪", "澜", "纯", "毓", "悦",
			"媛", "冰", "茜", "颖", "雪", "茗", "羽", "希", "宁", "欣", "滢", "馥", "璧",
			"璐", "影", "荔", "筠", "可", "兰", "凤", "洁", "梅", "琳琳", "素", "云", "莲",
			"真", "环", "爽", "菊", "霞", "香", "妹", "惠", "倩", "青", "月", "萍", "红",
			"莺", "嘉", "园", "勤", "露", "瑶", "爱", "燕", "贞", "莉", "桂", "娣", "翠",
			"叶", "琦", "春", "昭", "秋", "瑞", "凡", "锦", "琬", "珊", "艳", "莎莎", "竹",
			"霭", "瑾", "咏", "怡", "婵", "姣", "婉", "雁", "蓓", "飘", "育", "纨", "蓉",
			"眉", "君", "琴", "荷", "丹", "蕊", "娥", "菁", "婕", "琰", "韵", "融", "馨",
			"瑗", "宜", "凝", "晓", "欢", "篱", "枫", "慧", "荣", "岚", "晶", "范", "菲" };

	private static final String[] MALE_FIRST_NAMES = { "奇", "然", "涵", "诗", "梓",
			"怡", "伟峰", "平", "文思", "明", "志", "磊", "江", "哲", "勇", "毅", "彬", "斌",
			"梁", "启", "伟", "明", "俊", "勇", "毅", "诚", "峰", "强", "保", "东", "文",
			"博", "辉", "翰", "力", "厚", "健", "浩", "永", "宏", "致", "广", "志", "义",
			"兴", "仁", "哲", "盛", "雄", "壮", "建", "山", "家", "超", "时", "震", "琛",
			"钧", "波", "福", "生", "龙", "国", "胜", "学", "样", "昭", "秋", "瑞", "凡",
			"锦", "琬", "珊", "艳", "莎", "竹", "霭", "瑾", "咏", "怡", "婵", "姣", "婉",
			"雁", "蓓", "飘", "育", "纨", "蓉", "眉", "君", "琴", "荷", "丹", "蕊", "娥",
			"菁", "婕", "琰", "韵", "融", "馨", "瑗", "宜", "凝", "James", "John",
			"Robert", "Michael", "William", "David", "Richard", "Charles",
			"Joseph",

			"Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald",
			"George", "Kenneth", "Steven",

			"Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason",
			"Matthew", "Gary", "Timothy",

			"Jose", "Larry", "Jeffrey", "Frank", "Scott", "Eric", "Stephen",
			"Andrew", "Raymond",

			"Gregory", "Joshua", "Jerry", "Dennis", "Walter", "Patrick",
			"Peter", "Harold", "Douglas",

			"Henry", "Carl", "Arthur", "Ryan", "Roger", "Joe", "Juan", "Jack",
			"Albert", "Jonathan",

			"Justin", "Terry", "Gerald", "Keith", "Samuel", "Willie", "Ralph",
			"Lawrence", "Nicholas",

			"Roy", "Benjamin", "Bruce", "Brandon", "Adam", "Harry", "Fred",
			"Wayne", "Billy", "Steve",

			"Louis", "Jeremy", "Aaron", "Randy", "Howard", "Eugene", "Carlos",
			"Russell", "Bobby",

			"Victor", "Martin", "Ernest", "Phillip", "Todd", "Jesse", "Craig",
			"Alan", "Shawn",

			"Clarence", "Sean", "Philip", "Chris", "Johnny", "Earl", "Jimmy",
			"Antonio", "Danny",

			"Bryan", "Tony", "Luis", "Mike", "Stanley", "Leonard", "Nathan",
			"Dale", "Manuel", "Rodney",

			"Curtis", "Norman", "Allen", "Marvin", "Vincent", "Glenn",
			"Jeffery", "Travis", "Jeff",

			"Chad", "Jacob", "Lee", "Melvin", "Alfred", "Kyle", "Francis",
			"Bradley", "Jesus", "Herbert",

			"Frederick", "Ray", "Joel", "Edwin", "Don", "Eddie", "Ricky",
			"Troy", "Randall", "Barry",

			"Alexander", "Bernard", "Mario", "Leroy", "Francisco", "Marcus",
			"Micheal", "Theodore",

			"Clifford", "Miguel", "Oscar", "Jay", "Jim", "Tom", "Calvin",
			"Alex", "Jon", "Ronnie",

			"Bill", "Lloyd", "Tommy", "Leon", "Derek", "Warren", "Darrell",
			"Jerome", "Floyd", "Leo",

			"Alvin", "Tim", "Wesley", "Gordon", "Dean", "Greg", "Jorge",
			"Dustin", "Pedro", "Derrick",

			"Dan", "Lewis", "Zachary", "Corey", "Herman", "Maurice", "Vernon",
			"Roberto", "Clyde",

			"Glen", "Hector", "Shane", "Ricardo", "Sam", "Rick", "Lester",
			"Brent", "Ramon", "Charlie",

			"Tyler", "Gilbert", "Gene" };

	public static final String[] LAST_NAMES = {

	"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller",
			"Wilson", "Moore",

			"Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris",
			"Martin", "Thompson", "Garcia",

			"Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee",
			"Walker", "Hall", "Allen",

			"Young", "Hernandez", "King", "Wright", "Lopez", "Hill", "Scott",
			"Green", "Adams", "Baker",

			"Gonzalez", "Nelson", "Carter", "Mitchell", "Perez", "Roberts",
			"Turner", "Phillips",

			"Campbell", "Parker", "Evans", "Edwards", "Collins", "Stewart",
			"Sanchez", "Morris",

			"Rogers", "Reed", "Cook", "Morgan", "Bell", "Murphy", "Bailey",
			"Rivera", "Cooper",

			"Richardson", "Cox", "Howard", "Ward", "Torres", "Peterson",
			"Gray", "Ramirez", "James",

			"Watson", "Brooks", "Kelly", "Sanders", "Price", "Bennett", "Wood",
			"Barnes", "Ross",

			"Henderson", "Coleman", "Jenkins", "Perry", "Powell", "Long",
			"Patterson", "Hughes",

			"Flores", "Washington", "Butler", "Simmons", "Foster", "Gonzales",
			"Bryant", "Alexander",

			"Russell", "Griffin", "Diaz", "Hayes", "Myers", "Ford", "Hamilton",
			"Graham", "Sullivan",

			"Wallace", "Woods", "Cole", "West", "Jordan", "Owens", "Reynolds",
			"Fisher", "Ellis",

			"Harrison", "Gibson", "Mcdonald", "Cruz", "Marshall", "Ortiz",
			"Gomez", "Murray", "Freeman",

			"Wells", "Webb", "Simpson", "Stevens", "Tucker", "Porter",
			"Hunter", "Hicks", "Crawford",

			"Henry", "Boyd", "Mason", "Morales", "Kennedy", "Warren", "Dixon",
			"Ramos", "Reyes", "Burns",

			"Gordon", "Shaw", "Holmes", "Rice", "Robertson", "Hunt", "Black",
			"Daniels", "Palmer",

			"Mills", "Nichols", "Grant", "Knight", "Ferguson", "Rose", "Stone",
			"Hawkins", "Dunn",

			"Perkins", "Hudson", "Spencer", "Gardner", "Stephens", "Payne",
			"Pierce", "Berry",

			"Matthews", "Arnold", "Wagner", "Willis", "Ray", "Watkins",
			"Olson", "Carroll", "Duncan",

			"Snyder", "Hart", "Cunningham", "Bradley", "Lane", "Andrews",
			"Ruiz", "Harper", "Fox",

			"Riley", "Armstrong", "Carpenter", "Weaver", "Greene", "Lawrence",
			"Elliott", "Chavez",

			"Sims", "Austin", "Peters", "Kelley", "Franklin", "Lawson",

			"Beth", "Jeanne", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋",
			"沈", "韩", "杨", "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹",
			"严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦",
			"章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "苗", "韦", "马", "余",
			"蓝", "岑", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅",
			"庞", "熊", "纪", "舒", "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵",
			"席", "季", "麻", "贾", "娄", "江", "童", "颜", "郭", "梅", "林", "刁", "锺" };

	public final static String[] EMAIL_SUFFIX = { "qq.com", "126.com",
			"163.com", "gmail.com",

			"163.net", "msn.com", "hotmail.com", "yahoo.com.cn", "sina.com",
			"@mail.com", "263.net", "sohu.com",

			"21cn.com", "sogou.com"

	};

	public final static <T> T nextValue(T[] array) {

		assert (array != null && array.length > 0);
		Random r = new Random();
		return array[r.nextInt(array.length)];

	}

	public final static String getRandomEnglishName() {
		return getRandomEnglishFirstName() + " " + getRandomEnglishLastName();
	}

	public final static String getRandomEnglishFirstName() {
		Random r = new Random();
		if (r.nextBoolean())
			return nextValue(FEMALE_FIRST_NAMES);
		else
			return nextValue(MALE_FIRST_NAMES);
	}

	public final static String getRandomEnglishLastName() {
		return nextValue(LAST_NAMES);
	}

	public final static String getRandomEmailAddress() {
		return getRandomEnglishFirstName() + getRandomEnglishLastName() + "@"
				+ nextValue(EMAIL_SUFFIX);
	}

}
