INSERT INTO public."Library" ("name") VALUES 
('myLibrary')
;

INSERT INTO public.author ("lastName",firstname,age) VALUES 
('Cufio','Lidya',25)
,('Gro','Gro',29)
,('Callain','Clem',28)
,('strife','Cloud',31)
,('king','stephen',63)
,('myLastName','myFirstName',32)
;

INSERT INTO public.book ("name","type",idauthor) VALUES 
('COBOL 4 EVER','Technique',1)
,('I luv Java','Technique',2)
,('Paint Mon amour','Roman',1)
,('Vous ètes nuls','Roman',3)
,('L''épée broyante à travers les ages','Roman',4)
,('Comment créer une épée démontable','Technique',4)
,('Marche ou crève','Roman',5)
,('Rage','Roman',5)
,('Dr sleep','Roman',5)
,('myBook','Roman',3)
;

INSERT INTO public.librarybook (idbook,idlibrary) VALUES 
(1,1)
,(2,1)
,(3,1)
,(4,1)
,(5,1)
,(10,1)
,(11,1)
,(12,1)
,(13,1)
;


