import random

def joc_penjat():
    paraules = [
    "casa", "cotxe", "taula", "cadira", "ordenador",
    "xarxa", "servidor", "programacio", "microsistema", "pantalla",
    "teclat", "ratoli", "internet", "fitxer", "carpeta",
    "disc", "memoria", "usuari", "contrasenya", "seguretat",
    "correu", "missatge", "videojoc", "processador", "connexio"
    ]
    paraula = random.choice(paraules)
    lletres_encertades = set()
    intents_restants = 6

    print("Benvingut al joc del penjat!")
    print("------------------------------")

    while intents_restants > 0:
        estat = [l if l in lletres_encertades else '_' for l in paraula]
        print("Paraula: " + ' '.join(estat))
        print(f"Intents restants: {intents_restants}")

        lletra = input("Endevina una lletra: ").lower().strip()
        if len(lletra) != 1 or not lletra.isalpha():
            print("Si us plau, introdueix una sola lletra vàlida.")
            continue

        if lletra in lletres_encertades:
            print("Ja has provat aquesta lletra.")
            continue

        if lletra in paraula:
            lletres_encertades.add(lletra)
            print("Correcte!")
            if all(l in lletres_encertades for l in paraula):
                print(f"Enhorabona! Has guanyat. La paraula era: {paraula}")
                break
        else:
            intents_restants -= 1
            print("Incorrecte!")

    if intents_restants == 0:
        print(f"Has perdut! La paraula era: {paraula}")

joc_penjat()