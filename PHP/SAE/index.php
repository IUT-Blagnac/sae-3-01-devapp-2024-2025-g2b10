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
            <div class="image-text-section"> 
                <img src="images/logo_abracadabra.png" alt="Description de l'image" class="center-image full-width-image"> 
                <div class="text-overlay">
                    <h1>La Magie à portée de main</h1> 
                    <p>Découvrez nos objets magiques : balais volants, baguettes et bien plus encore dans notre boutique en ligne.</p> 
                    <button class="explore-button">Explorer</button> 
                </div> 
            </div>
        </main>
    </div>
</div>

<!-- Pied de page & fin html -->
<?php require_once('./include/footer.php'); ?>