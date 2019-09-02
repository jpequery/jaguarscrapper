# jaguarscrapper

TODO: 
- reconstruire le bbcode (si vraiment utile, mais il me semble que c'est le format de stockage)
- détecter les images dans les posts, et extraire les images pour les ré-héberger
- detecter les smileys pour reconstruire la base de smiley
- installer un forum pour test (pb mysql actuellement)
- construire les requetes d'injections de données dans le nouveau forum
- gérer corredctement les paramètres dans un fichier ini

DONE:
- parcours du forum, des sous forums et de tous les topics, y compris les multipages
- gestion du login (récupérer les id de sessions avec chrome)
- identification des comptes, parsing de la page, affectation d'un post au compte
- connection à la base de données MySql destination
- gestion d'erreur : topic inexistant mais encore référencé, timeout, reprise sur erreur

