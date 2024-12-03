<!-- partie html & head -->
<?php require_once('./include/head.php'); ?>

<!-- partie body -->
<?php require_once('./include/header.php'); ?>

<!-- Conteneur principal -->
<div class="container-fluid flex-grow-1">
    <div class="row">

        <!-- partie menu -->
        <?php require_once('./include/menu.php'); ?>

        <!-- partie contenu principal -->
        <main role="main" class="col-md-9 ms-sm-auto col-lg-10 px-4">
            <div class="account-section">
                <h1>Connexion</h1><br>
                <form action="login.php" method="post" class="account-form">
                    <div class="form-group">
                        <label for="email">Email :</label>
                        <input type="email" id="email" name="email" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Mot de passe :</label>
                        <input type="password" id="password" name="password" required>
                        <a href="#" class="forgot-password">Mot de passe oublié ?</a>
                    </div>
                    <div class="form-group form-group-inline">
                        <input type="checkbox" id="remember" name="remember">
                        <label for="remember">Se souvenir de moi</label>
                    </div>
                    <button type="submit" class="login-button">Se connecter</button>
                </form>
                <p class="no-account"><a href="creationCompte.php" style="text-decoration: none; color: inherit;">Je n'ai pas encore de compte</a></p>
            </div>
        </main>
    </div>
</div>

<!-- Pied de page & fin html -->
<?php require_once('./include/footer.php'); ?>