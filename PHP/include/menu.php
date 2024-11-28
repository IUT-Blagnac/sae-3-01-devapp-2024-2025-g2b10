<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menu Pleine Largeur</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box; /* Gérer les marges/paddings */
        }

        body {
            font-family: Arial, sans-serif;
        }

        header {
            width: 100%;                   /* Occuper toute la largeur de la page */
            display: flex;                 /* Flexbox pour organiser les éléments */
            justify-content: space-between;/* Séparer les groupes gauche et droite */
            align-items: center;           /* Centrer verticalement les éléments */
            padding: 10px 20px;            /* Espacement interne */
            background-color: #fff;        /* Fond blanc */
            border-bottom: 1px solid #ddd; /* Ligne en bas */
        }

        .logo {
            font-family: "Cursive", sans-serif; /* Style du texte du logo */
            font-size: 1.5rem;                 /* Taille du texte */
            font-weight: bold;                 /* Gras */
            color: #333;                       /* Couleur sombre */
        }

        .menu {
            flex: 1;                    /* Le menu prend tout l'espace disponible */
            display: flex;              /* Organisation des liens en ligne */
            justify-content: center;    /* Centrer les éléments horizontalement */
            list-style: none;           /* Supprimer les puces */
            padding: 0;
            margin: 0;
        }

        .menu li {
            margin: 0 20px;             /* Espacement entre chaque item */
            display: flex;
            align-items: center;        /* Aligner l'image et le texte */
        }

        .menu a {
            text-decoration: none;      /* Pas de soulignement */
            color: #333;                /* Couleur du texte */
            font-size: 1rem;            /* Taille du texte */
            font-weight: 500;           /* Texte légèrement en gras */
            padding: 5px 10px;          /* Espacement interne */
            transition: color 0.3s, border-bottom 0.3s; /* Effet fluide au survol */
        }

        .menu a:hover {
            color: #581845;             /* Changement de couleur au survol */
            border-bottom: 2px solid #581845; /* Ligne en bas au survol */
        }

        .cart {
            display: flex;              /* Aligner l'icône panier et le texte */
            align-items: center;        /* Centrer verticalement */
            margin-left: 20px;          /* Espacement entre le menu et le panier */
            font-size: 1.2rem;          /* Taille de l'icône */
            color: #333;
        }

        .cart img {
            width: 25px;                /* Taille de l'icône panier ajustée */
            height: 25px;               /* Forcer la hauteur pour la cohérence */
            margin-right: 5px;          /* Espacement entre l'icône et le texte */
        }

        .cart span {
            font-size: 0.9rem;          /* Taille légèrement réduite pour le texte */
        }
    </style>
</head>
<body>
    <header>
        <!-- Logo à gauche -->
        <div class="logo">Abraca-Dabra-Cadabra</div>

        <!-- Menu au centre -->
        <div class="options">
            <ul class="menu">
                <li><a href="#">Accueil</a></li>
                <li><a href="#">Catégories</a></li>
                <li><a href="#">Recherche</a></li>
                <!-- Panier à droite -->
                <li class="cart">
                    <a href="#"><img src="https://img.icons8.com/?size=100&id=22167&format=png&color=000000" alt="Panier"></a>
                    <span>(0)</span>
                </li>
                <li class="cart">
                    <!-- Deuxième image, icône panier -->
                    <a href="#"><img src="https://img.icons8.com/?size=100&id=83190&format=png&color=000000" alt="Icône Panier"></a>
                </li>
            </ul>
        </div>
    </header>
</body>
</html>
