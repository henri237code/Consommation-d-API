import json
from dataclasses import asdict, dataclass

from fastapi import FastAPI, HTTPException, Path
from pydantic import BaseModel

# Dictionnaire de donnees
with open("acteurs.json", "r") as f:  # ouvrir le fichier json en mode lecture
    acteurs_list = json.load(f)  # Lecture du fichier f et conversion en objet python

list_acteurs = {k + 1: v for k, v in enumerate(acteurs_list)}


@dataclass
class Acteur():
    id: int
    name: str
    bio: str
    picture: str


class ActeurUpdate(BaseModel):
    name: str
    bio: str
    picture: str


app = FastAPI()


# Persistance des donnees
def save_to_file():
    with open("acteurs.json", "w") as f:
        json.dump(list(list_acteurs.values()), f, indent=4)
@app.get("/")
def get_to() :
    return "http://127.0.0.1:8000/acteurs"

@app.get("/total_acteurs")
def get_total_acteurs() -> dict:
    return {"total": len(list_acteurs)}


@app.get("/acteurs")
def get_all_acteurs() -> list[Acteur]:
    res = []
    for id in list_acteurs:
        res.append(Acteur(**list_acteurs[id]))  # Conversion en dictionnaire grace a **
    return res


@app.get("/acteurs/{id}")
def get_acteurs_by_id(id: int = Path(ge=1)) -> Acteur:  # ge ie >=
    if id not in list_acteurs:
        raise HTTPException(status_code=404, detail="Cet acteur n'existe pas")
    return Acteur(**list_acteurs[id])


@app.post("/acteur/")
def create_acteur(acteur: Acteur) -> Acteur:
    if acteur.id in list_acteurs:
        raise HTTPException(status_code=409, detail=f"Cet acteur {acteur.id} existe deja")
    list_acteurs[acteur.id] = asdict(acteur)  # asdict transforme un objet python en dictionnaire
    save_to_file()
    return acteur


@app.put("/acteur/{id}")
def update_acteur(acteur: ActeurUpdate, id: int = Path(ge=1)) -> Acteur:
    if id not in list_acteurs:
        raise HTTPException(status_code=404, detail=f"Cet acteur {id} n'existe pas")
    update_acteur = Acteur(id=id, name=acteur.name, bio=acteur.bio, picture=acteur.picture)
    list_acteurs[id] = asdict(update_acteur)
    save_to_file()
    return update_acteur


@app.delete("/acteur/{id}")
def delete_acteur(id: int = Path(ge=1)) -> Acteur:
    if id not in list_acteurs:
        raise HTTPException(404, f"Cet acteur {id} n'existe pas")
    acteur = Acteur(**list_acteurs[id])
    del list_acteurs[id]
    save_to_file()
    return acteur