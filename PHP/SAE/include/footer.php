<!-- File: footer.php -->
<footer>
    <div class="container footer-container">
        <!-- Colonne gauche -->
        <div class="footer-column">
            <h5><strong>Magie</strong></h5>
            <p>Découvrez nos objets magiques uniques et fascinants.</p>
            &copy; 2024. Tous droits réservés.
        </div>

        <!-- Colonne milieu -->
        <div class="footer-column">
            <h5><strong>Produits</strong></h5>
            <p>+33 1 23 45 67 89</p>
            <p>contact@abracadabracadabra.com</p>
        </div>

        <!-- Colonne droite -->
        <div class="footer-column contact-form">
            <h5>Contact</h5>
            <form>
                <input type="email" placeholder="Entrez votre adresse email">
                <button type="submit">Envoyer</button>
            </form>
        </div>
    </div>
</footer>

<style>
    html, body {
        height: 100%; /* Assure que la page occupe 100% de la hauteur */
    }

    body {
        display: flex; /* Flexbox pour organiser les sections */
        flex-direction: column; /* Disposition verticale */
    }

    .container-fluid {
        flex: 1; 
    }

    footer {
        position: relative; 
        bottom: 0;
        width: 100%; /* Prend toute la largeur */
        background-color: #121212;
        color: white;
        text-align: center; /* Centrer le texte dans le footer */
        padding: 5px 0;
        font-size: 12px;
    }

    h5 {
        font-size: 13px;
    }

    .footer-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
    }

    .footer-column {
        flex: 1 1 20%;
        text-align: left;
        padding: 10px;
    }

    .footer-column p {
        margin: 1px 0;
    }

    .contact-form input[type="email"] {
        width: 50%; /* Réduire la largeur */
        padding: 4px; /* Réduire le padding */
        border-radius: 15px;
        border: 1px solid #ccc;
        margin-right: 5px;
    }

    .contact-form button {
        padding: 6px 18px;
        border-radius: 15px;
        border: none;
        background-color: #6c63ff;
        color: white;
        cursor: pointer;
    }

    .contact-form button:hover {
        background-color: #5145cc;
    }

    @media (max-width: 768px) {
        .footer-container {
            flex-direction: column;
        }

        .footer-column {
            flex: 1 1 100%;
            margin-bottom: 5px;
        }

        .contact-form input[type="email"] {
            width: 100%;
            margin-bottom: 8px;
        }
    }
</style>