# Parking_System


#How to Work with git 


- Para começar uma branch apartir da branch developer
  git checkout -b nomedafeature developer

- Para dar merge ( estar dentro da branch que pretendemos dar o merge )
  git merge --no-ff nomedafeature

- Apagar banch local
  git branch -d nomedafeature

- Push para a developer
  git push origin developer

- ATENÇÃO SENSIVEL ( SÓ APAGAR DEPOIS DE VERIFICADO E TESTADO )
  git push --delete origin myFeature

- Ver log das branchs
  git log --all --decorate --oneline --graph

