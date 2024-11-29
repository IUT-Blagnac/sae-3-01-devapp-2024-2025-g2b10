<!DOCTYPE html>
<html lang="fr">
<style>
    body {
        margin: 0;
        padding: 0;
        display: flex;
        flex-direction: column;
        min-height: 100vh;
    }

    footer {
        background-color: #121212;
        color: white;
        padding: 5px 0; 
        font-size: 9px; 
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
        padding: 5px; 
    }

    .footer-column img {
        width: 30px; 
        margin: 5px 3px; 
    }

    .footer-column p {
        margin: 1px 0; 
    }

    .contact-form input[type="email"] {
        width: 70%;
        padding: 6px;
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
<body>
    <footer>
        <div class="container footer-container">
            <!-- Colonne gauche -->
            <div class="footer-column">
                <h5><strong>Magie</strong></h5>
                <p>Découvrez nos objets magiques uniques et fascinants.</p>
                <div>
                    <img src="https://img.icons8.com/material-outlined/50/ffffff/facebook.png" alt="Facebook">
                    <img src="https://img.icons8.com/material-outlined/50/ffffff/instagram-new.png" alt="Instagram">
                    <img src="https://img.icons8.com/?size=100&id=fJp7hepMryiw&format=png&color=FFFFFF" alt="Twitter">
                </div>
                &copy; 2024. Tous droits réservés.
            </div>

            <!-- Colonne milieu -->
            <div class="footer-column">
                <h5><strong>Produits</strong></h5>
                <p>+33 1 23 45 67 89</p>
                <p>contact@abracadabra</p>
                <p>cadabra.com</p>
            </div>

            <!-- Colonne droite -->
            <div class="footer-column contact-form">
                <h5>Contact</h5><br>
                <form>
                    <input type="email" placeholder="Entrez votre adresse email">
                    <button type="submit">Envoyer</button>
                </form>
            </div>
        </div>
    </footer>
</body>
</html>
