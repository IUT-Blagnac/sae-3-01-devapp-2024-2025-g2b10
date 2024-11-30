
<header class="nav">
    <!-- Logo à gauche -->
    <div class="logo">Abraca-Dabra-Cadabra</div>

    <!-- Menu au centre -->
    <div class="menu">
        <ul class="options">
            <li><a href="#">Accueil</a></li>
            <li><a href="#">Catégories</a></li>
            <li><a href="#">Recherche</a></li>
            <!-- Panier à droite -->
            <li class="icon">
                <a href="#"><img src="https://img.icons8.com/?size=100&id=22167&format=png&color=000000" alt="Panier"></a>
                <span>(0)</span>
            </li>
            <li class="icon">
                <!-- Deuxième image, icône compte -->
                <a href="compte.php"><img src="https://img.icons8.com/?size=100&id=83190&format=png&color=000000" alt="Compte"></a>
            </li>
        </ul>
    </div>
</header>

<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
        font-family: "Glass Antiqua", cursive;
        font-weight: 400;
        font-style: normal;
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
        font-size: 1.5rem;                 /* Taille du texte */
        font-weight: bold;                 /* Gras */
        color: #333;                       /* Couleur sombre */
    }

    .options {
        flex: 1;                    /* Le menu prend tout l'espace disponible */
        display: flex;              /* Organisation des liens en ligne */
        justify-content: center;    /* Centrer les éléments horizontalement */
        list-style: none;           /* Supprimer les puces */
        padding: 0;
        margin: 0;
    }

    .options li {
        margin: 0 20px;             /* Espacement entre chaque item */
        display: flex;
        align-items: center;        /* Aligner l'image et le texte */
    }

    .options a {
        text-decoration: none;      /* Pas de soulignement */
        color: #333;                /* Couleur du texte */
        font-size: 1rem;            /* Taille du texte */
        font-weight: 500;           /* Texte légèrement en gras */
        padding: 5px 10px;          /* Espacement interne */
        transition: color 0.3s, border-bottom 0.3s; /* Effet fluide au survol */
    }

    .options a:hover {
        color: #581845;             /* Changement de couleur au survol */
        border-bottom: 2px solid #581845; /* Ligne en bas au survol */
    }

    .icon {
        display: flex;              /* Aligner l'icône panier et le texte */
        align-items: center;        /* Centrer verticalement */
        margin-left: 20px;          /* Espacement entre le menu et le panier */
        font-size: 1.2rem;          /* Taille de l'icône */
        color: #333;
    }

    .icon img {
        width: 25px;                /* Taille de l'icône panier ajustée */
        height: 25px;               /* Forcer la hauteur pour la cohérence */
        margin-right: 5px;          /* Espacement entre l'icône et le texte */
    }

    .icon span {
        font-size: 0.9rem;          /* Taille légèrement réduite pour le texte */
    }
</style>
