# Project PA

 ISCTE-IUL Master in Computer Engineering curricular unit of the 2º semester with 4 phases.

- Phase 1 - Data Structure and Simple Operations
- Phase 2 - Reflection
- Phase 3 - XML Editor
- Phase 4 - XML Editor with Plugins

# **How to use:**
Para criar um ficheiro XML pode começar por definir a versão e a codificação do seu  ficheiro instanciando a classe "Prolog" sendo a mesma optativa.

De seguida, necessita de criar a primeira entidade do ficheiro XML sendo que esta irá atuar como  a raiz do ficheiro logo irá conter todas as outras entidades.
Visto que esta "Entity" atua como raiz irá ter como nulo o atributo referente ao seu parent.

Para relacionar herarquicamente as diversas "Entity" necessita de definir o seu progenitor quando instancia a classe.

Para efeitos de simplificação, uma "Entity" apenas pode conter ou texto ou um conjunto de outras "Entity".
Uma  "Entity" pode ter ou não atributos.

A classe Entity contem uma lista da classe Entity, uma lista da classe Attribute e vários atributos do tipo String.
A classe Attribute contem um nome e um valor ambos String.
***
# **Objects Definition Example:**

* ### Create XML with different "Entity" and a "Prolog":  

```
    val xmlheader = Prolog("UTF-8","1.0")

    val xmlobject = Entity("Bookstore",null)
    xmlobject.attribute.add( Attribute("Owner","Lourenco"))
    xmlobject.attribute.add( Attribute("Category","Good Books"))

    val children1 = Entity("1984",xmlobject)
    children1.setText("Random text")
    children1.attribute.add(Attribute("ID","Book1"))

    val children2 = Entity("Odyssey", xmlobject)
    children2.attribute.add(Attribute("ID","Book2"))

    val children3 = Entity("Chapter1",children2)
    children3.setText("Random long text")
    children3.attribute.add(Attribute("Name","Intro"))

    val children4 = Entity("Chapter2", children2)
    children4.setText("Random long text")
```

# **Simple Operations:**

* ### Serialize a XML document to a textual String:
```
val header: String = serializationheader(p)
var text : String = serialization(xmlobject, header)
```

* ### Find an "Entity" with a specific attribute number:
```
var result = find(xmlobject,nAttribute(2))
```

* ### Find an "Entity" with a specific number of children :
```
var result = find(xmlobject,nChild(2))
```


* ### Find an "Entity" with a specific name:
```
var result = find(xmlobject,entityName("Chapter1"))
```

***
# **Reflection Usage:**


* ### Define a data class with annotations :
```
data class Book(
    @XmlName("ID")
    val id:  Int?,
    @XmlName("Book Name")
    val name: String?,
    val read: Boolean?,
    val category: Enum<Categories>?,
    @XmlTagContent
    val chapters: List<Chapter>?,
)

data class Chapter(
    @XmlName("ID")
    val id:  Int?,
    @XmlTagContent
    val Text: String
)
```
* ### Call the createXML function with optional fields for Prolog class creation:
```
var c1:    Chapter = Chapter(1, "Texto do capitulo 1")
var c2:    Chapter = Chapter(1, "Texto do capitulo 2")
var mobyDick: Book = Book(   1, "Moby Dick", false, Categories.Fiction, listOf(c1, c2))

var xml = createXML(mobyDick, "1.0", "UTF-8")
```

***
# **XML Editor:**








