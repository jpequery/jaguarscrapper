# jaguarscrapper

[Edit with gitpodio](https://gitpod.io/#https://github.com/jpequery/jaguarscrapper)

Contexte : chuuuttt

TODO: 
- reconstruire le bbcode (si vraiment utile, mais il me semble que c'est le format de stockage)(c'est pas méga utile, on peut vivre sans à titre de compatibilité ascendante)
- détecter les images dans les posts, et extraire les images pour les ré-héberger
- detecter les smileys pour reconstruire la base de smiley
- construire les requetes d'injections de données dans le nouveau forum
     - forum  (manque ACLet optimisations)
     - topic (manque optimisations)
     - post (manque optimisation)
     - user (ils sont reconnus, reste les details, age, occupation...)
     - smiley
     - group
- gestion des groupes, reconstruire la liste des membres de l'asso

DONE:
- remettre des dates utilisables, pas de hier, remettre les années 2019 (a optimiser)
- gérer correctement les paramètres dans un fichier ini
- installer un forum pour test (pb mysql actuellement)
- parcours du forum, des sous forums et de tous les topics, y compris les multipages
- gestion du login (récupérer les id de sessions avec chrome)
- identification des comptes, parsing de la page, affectation d'un post au compte
- connection à la base de données MySql destination
- gestion d'erreur : topic inexistant mais encore référencé, timeout, reprise sur erreur
- utilisation du visibility
- gestion des utilisateurs sans compte existant (cf kaprice)

Note technique sur la connection à distance : https://stackoverflow.com/questions/1559955/host-xxx-xx-xxx-xxx-is-not-allowed-to-connect-to-this-mysql-server

Explication des tables phpBB : https://ftp.phpbb-fr.com/cdd/phpbb2/charlie/tables/index.html (ancienne version2...)
verrsion 3.0 : https://ftp.phpbb-fr.com/cdd/phpbb3/_screens/doc_table/?table=phpbb_users

Pour faire apparaitre le forum : forum_type à 1, forum_flags à 48 (ok pour la racine). Voir avec les ACL.

![etat actuel](https://i.imgur.com/OF9tAOT.png "etat actuel")

