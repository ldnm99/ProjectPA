# Project PA

 ISCTE-IUL Master in Computer Engineering curricular unit of the 2º semester with 4 phases
# **Data Structure**


***
# **How to use:**
Para criar um ficheiro XML pode começar por definir a versão e a codificação do seu  ficheiro instanciando a classe "Prolog" sendo a mesma optativa.

De seguida, necessita de criar a primeira entidade do ficheiro XML sendo que esta irá atuar como  a raiz do ficheiro logo irá conter todas as outras entidades.
Visto que esta "Entity" atua como raiz irá ter como nulo o atributo referente ao seu parent.

Para relacionar herarquicamente as diversas "Entity" necessita de definir o seu progenitor quando instancia a classe.

Para efeitos de simplificação, uma "Entity" apenas pode conter ou texto ou um conjunto de outras "Entity".
Uma  "Entity" pode ter ou não atributos.

***
# **Examples:**

* ### Create XML with different "Entity" and a "Prolog":  

```
    val p = Prolog("UTF-8","1.0")

    val xmlobject = Entity(null)
    xmlobject.name = "Root"
    xmlobject.attributes.add("Something")

    val children1 = Entity(xmlobject)
    children1.name = "Children1"
    children1.value = "Random long text"
    children1.attributes.add("Attribute1")

    val children2 = Entity(xmlobject)
    children2.name = "Children2"
    children2.attributes.add("Attribute2")

    val children3 = Entity(children2)
    children3.name = "Children3"
    children3.value = "Random long text"
    children3.attributes.add("Attribute3")

    val children4 = Entity(children2)
    children4.name = "Children4"
    children4.value = "Random long text"
```



* ### Serialize a XML to textual:
```
val header: String = serializationheader(p)
var text : String = serialization(xmlobject, header)
```

* ### Find a "Entity" with a specific name:
```
var entitysearched = findEntity(xmlobject,"Children1")
```
