package ProvaNP2;
//  Robotron - a robot by (Ewerton,Diego e Rodrigo)
//UNIP – CC6P20-Sistemas Inteligentes
//Professor Ricardo Piantola
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.*;
public class Robotron extends AdvancedRobot{	
//Movimentação
               double movimento=50000;  //Movimento básico
               double mov_frente=50; //Movimento padrão
               double mov_tras=70; // Movimento padrão
               boolean movFrente; //Verdadeiro se setAhead for utilizado e falso para setback
               boolean toqueiParede ; //Verdadeiro quando proximo parede
			   static double movimento2;//Tatica secundaria de movimentação
//outros dados do Robotron
 double minhaVida;
 double GunHeat;
static int cont=0; // contador
int y=0; // metrica para a estrategias de combate
double dist=(70);//metrica para começo de partida
// Tiros,bala,munição
              int acertado=0; //Conta tiros recebidos
			  double tiros_errados;//Contagem dos tiros errados
			  int tomeiTiro=0;//Contagem dos tiros tomados
              double anguloTiroRec; //Angulo do tiro recebido
              double acertos; //Contagem de tiros acertados
	double erros; //Contagem de tiros errados
              double tiro=0;// métrica para estratégia ofensiva
               static double anguloTiro; //Ângulo para girar o canhão
               static double anguloRadar; //Angulo para girar o radar	
	// Variaveis dados do inimigo
              double vidaInimigo, velocidadeInimigo, distancia_adversario,angulo; //Vai pegar velocidade, vida, distancia e Angulo do inimigo
 static  double posicaoInimigo; // Posição do inimigo
 //pegando nome,outros
             String trackName;
			 int others;//inimigos
// Começando
                            public void run() {
// Cores do robo
		setBodyColor(Color.yellow); // corpo
		setGunColor(Color.blue); // arma
		setRadarColor(Color.green); // radar
		setBulletColor(Color.black); //bala
		setScanColor(Color.red); //scanner
//Cada parte do robô se move de modo independente das outras
		setAdjustRadarForRobotTurn(true); 
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		others = getOthers();//conta inimigos
		if(getX()<=40)
                                toqueiParede = true;
			else
                                toqueiParede = false;
//Robô gira o radar 360 graus para encontrar o inimigo
		            setTurnRadarRight(360);
			movFrente = true; //Quando setAhead for utilizado, movFrente será while(true){ //Laço robocode 
  while(true){	// Desviar parede
if (getX() <= 50 || getX() >= getBattleFieldWidth() - 50 || getY() <= 50 || getY() >= getBattleFieldHeight() - 50)
	inverter();
if (toqueiParede = true) //Verifica se o robô está próximo da parede
	inverter();
if (getRadarTurnRemaining() == 0.0) //Caso o radar perder o inimigo, voltar a checar
		setTurnRadarRight(360);
if (getDistanceRemaining() == 0) //Se o robô terminou o seu movimento, inverter direção	
				setAhead(-1);
					execute(); // execução robocode 
					}
				} // fim do metodo rum
public void inverter() { //Método de inversão de direção
		if (movFrente) {
			setBack(movimento);
			movFrente = false;
		} 
else {
			setAhead(movimento);
			movFrente = true;
		}
	}// fim do metodo inverter
public void onHitWall(HitWallEvent e){
		inverter(); //Inverter direção caso bater na parede   
 }
public void onSkippedTurn(SkippedTurnEvent e) {
			setTurnGunRight(anguloTiro);
		}
 public void onScannedRobot(ScannedRobotEvent e) { // Caso encontre um inimigo
                            velocidadeInimigo = e.getVelocity(); //Velocidade inimigo
		vidaInimigo = getEnergy(); //Vida inimigo
		minhaVida = getEnergy(); //Vida robô
		 getGunHeat();//Calor canhão robô
		angulo = e.getBearing(); //Ângulo do adversário em relação ao robô                   
		//Distância do robô adverário até este
		distancia_adversario = e.getDistance();
// calculo da  posição inimigo
posicaoInimigo=getHeading()+e.getBearing();
anguloTiro=normalRelativeAngleDegrees(posicaoInimigo-getGunHeading()+tiro);
anguloRadar=normalRelativeAngleDegrees(posicaoInimigo-getRadarHeading()); // Formula Radar
// Formula movimentação robo	
if(movFrente) // mover para a frente
setTurnRight(normalRelativeAngleDegrees(e.getBearing() + mov_frente));
else // mover para trás
setTurnRight(normalRelativeAngleDegrees(e.getBearing() + mov_tras));
// Controle da distancia em relação ao tiro  que sera dado
if(Math.abs(anguloTiro)<=5){ // Angulo fechado, distancia muito pequena
                       setTurnRadarRight(anguloRadar); //Apontar radar para o inimigo
			setTurnGunRight(anguloTiro); //Girar canhão para atirar
			fire(3); //Atira
}
//Ângulos mais abertos, apenas girar canhão e radar
		else {
			setTurnGunRight(anguloTiro);
			setTurnRadarRight(anguloRadar);
		}
if (getGunHeat() == 0) { //Certificar-se que pode-se atirar
			//Fórmula de tiro adaptada conforme distância
			if (distancia_adversario >= 400 && distancia_adversario <= 500)
				fire(3 - distancia_adversario *0.004);
			if (distancia_adversario >= 300 && distancia_adversario <= 499)
				fire(3 - distancia_adversario*0.003 );
			if (distancia_adversario > 500)
				fire(0.5);
			else
				fire(3);
}
//Radar deve voltar a trabalhar caso o ângulo do canhão não for bom
		if (anguloTiro == 0)	
scan();
    } // fim do metodo radar
  public void onHitRobot(HitRobotEvent e){
	fire(3);
	setAhead(-movimento);	
		}// fim evento onHitRobot
     public void onBulletMissed(BulletMissedEvent e){
			tiros_errados=0;
//Caso estiver longe da parede e movendo-se para frente
		if (getX() > 50 && movFrente == true && toqueiParede != true){
                                            toqueiParede = true;
			mov_frente = -mov_frente*6;
                                              tiros_errados++;
			if (tiros_errados == 1){
				toqueiParede = true;
				mov_frente = -mov_frente*6;
			}
		}
else
mov_frente = 60;
}//Fim do evento onBulletMissed
              public void onBulletHit(BulletHitEvent e){
                                                e.getEnergy();
                                               acertos=0;// contagem
acertos++;
  if(tiro>acertos){   
  anguloTiro=normalRelativeAngleDegrees(posicaoInimigo- getGunHeading()+velocidadeInimigo);
}
else{
anguloTiro=normalRelativeAngleDegrees(posicaoInimigo-getGunHeading()+tiro);		
		}
	}// Fim do metodo onBulletHit
	// Quando o robo for atingido por um tiro
	  public void onHitByBullet(HitByBulletEvent e){
		tomeiTiro++; //Calcular tiros recebidos
		anguloTiroRec= e.getBearingRadians(); //Ângulo da bala
movimento2 = -5000/Math.sin(anguloTiroRec); //Ficar longe do ângulo da bala recebida
//Movimentação ao receber tiro
		setTurnRight(normalRelativeAngleDegrees(anguloTiroRec + movimento2));
if(tomeiTiro >= 2)
			inverter(); //Mudar direção movimento caso receber vários tiros
	} // fim do metodo receber tiro
           public void onBulletHitBullet(BulletHitBulletEvent e){           //Balas se chocam 
	  getBulletHitEvents();
	if(acertos==tiros_errados){
	setTurnRight(normalRelativeAngleDegrees(posicaoInimigo+ mov_frente));
              }
  }//Fim do evento
  //Estrategia de contra ataque
     public void estrategia(double minhaVida,double vidaInimigo,double calorCanhao){
	if(getX()<=distancia_adversario){
if(minhaVida<=50){
if(vidaInimigo<=50){
	if (distancia_adversario >= 1 && distancia_adversario <=101)
                                      y=0;
if (distancia_adversario >= 102 && distancia_adversario <= 201)
y=1;
if (distancia_adversario >= 202 && distancia_adversario <= 300)
y=0;
    }
}
if (vidaInimigo >= 51){
if (distancia_adversario >= 1 && distancia_adversario <= 101)
y=1;				
if (distancia_adversario >= 102 && distancia_adversario <= 201)
	y=1;		
if (distancia_adversario >= 202 && distancia_adversario <= 300)
	y=1;		
				}
if (minhaVida >= 51){
if (vidaInimigo <= 40){
if (distancia_adversario >= 1 && distancia_adversario <= 101)
y=0;			
if (distancia_adversario >= 102 && distancia_adversario <= 201)
y=0;					
if (distancia_adversario >= 202 && distancia_adversario <= 300)
y=0;
           }					
if (vidaInimigo >= 61){
			if (distancia_adversario >= 1 && distancia_adversario <= 101)
				y=1;			
			if (distancia_adversario >= 102 && distancia_adversario <= 201)
				y=1;			
			if (distancia_adversario >= 202 && distancia_adversario <= 300)
				y=0;						
                     } 
					}                                        
     else                   
             y=2;          // outras possibilidades                              
//Aplicando decisão
//Decisão contra-ataque
if (y == 1){ // modificar movimento
mov_frente = 50+(30*Math.sin(angulo)*0.1); //Nova fórmula de movimentação para frente
	mov_tras = mov_frente+30; //Nova fórmula de movimentação para trás
				}
if (y == 0){ // não modificar movimento
      mov_frente = 50; //Manter a mesma movimentação
         mov_tras = 70; //Manter a mesma movimentação
}
if (y == 2){
mov_frente =80+(50*Math.sin(angulo)*0.1); // nova movimentação para frent
 mov_tras = mov_frente+30; //Nova fórmula de movimentação para tras
		}//Fim Estrategica de contra ataque
//Estrategia Ofensiva
if (minhaVida <= 50){
if (vidaInimigo <= 50){
if (distancia_adversario >= 1 && distancia_adversario <= 100)
							y = 1;
	if (distancia_adversario >= 101&& distancia_adversario <= 200)
							y = 1;
		if (distancia_adversario >= 201 && distancia_adversario <= 300)
							y = 0;
					}
}
if (vidaInimigo >= 51){
		if (distancia_adversario >= 1 && distancia_adversario <= 100)
						y = 0;
			if (distancia_adversario >= 101 && distancia_adversario <= 200)
						y = 0;
			if (distancia_adversario >= 201 && distancia_adversario <= 300)
						y = 0;
				}
if (minhaVida >= 51){
if (vidaInimigo <= 50){
			if (distancia_adversario >= 1 && distancia_adversario <= 100)
							y = 1;
			if (distancia_adversario >= 101 && distancia_adversario <= 200)
							y = 1;
			if (distancia_adversario >= 201 && distancia_adversario <= 300)
							y = 1;
					}
					if (vidaInimigo >= 51){
				if (distancia_adversario >= 1 && distancia_adversario <= 100)
							y = 1;
			if (distancia_adversario >= 101 && distancia_adversario <= 200)
							y = 1;
			if (distancia_adversario >= 201 && distancia_adversario <= 300)
							y = 0;
					}
}
else {
y = 2;
}
//Aplicando decisão 
//Decisão ofensiva:
if (y == 1){ // modificar características do tiro
tiro = Math.PI * Math.sin(angulo) - velocidadeInimigo * 0.5; //Variável tiro com novo valor
				}
if (y == 0){ //Decisão ofensiva: não modificar características do tiro
	tiro=0;	
			}
if (y == 2){
tiro = Math.PI * Math.sin(angulo) - velocidadeInimigo * 0.65;
	}// Fim da heuristica
tiro++;
} // Fim estrategia ofensiva
}//Fim estrategia
	public void onWin(WinEvent e) {
			if(getEnergy()>80){
			rizadinha();	
			}
			else
			dancinha3();
		}
		public void onRobotDeath(RobotDeathEvent e) {
			if (trackName == e.getName()) {
				trackName = null;
			}
		}
			public void onDeath(DeathEvent e) {
		if (others == 0) {
			return;
		}
		if ((others - getOthers()) / (double) others < .75) {
			  dist+= 90;
			if (dist== 270) {
				dist=-90;
			}
			out.println("I died and did poorly... switching dist to " + dist);
		} 
         else {
			out.println("I died but did well.  I will still use dist " + dist);
		}
	}
		public void rizadinha() {
			for (int i = 0; i < 50; i++) {
				turnRight(30);
				turnLeft(30);
			}
		}
       public void dancinha3() { 
		setMaxVelocity(5);
		setTurnGunRight(10000);
		while(true) {
			ahead(20);
			back(20);  
			if (getEnergy() > 0.1) {
				setFire(1);
			}
	      }
		 }
       }// Fim da classe principal