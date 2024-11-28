<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Abraca-dabra-cadabra</title>

	
    <script>
		// Cette fonction permettra de demander confirmation avant la suppression d'un produit
		function confirmSuppr(idProd) {
			if(confirm("Etes-vous sûr de vouloir supprimer ce produit ?")){
				document.location.href = "SupprProd.php?pIdProd="+idProd;
			} else {
				alert("Suppression annulée");
				return false;
			}
		}
	</script>
</head>