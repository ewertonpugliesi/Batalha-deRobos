package ProvaNp2;
import robocode.*;
import java.awt.Color;
public class aSobrevivir extends TeamRobot
{
// Variaveis utilizadas na  equação;
//Constante matemática utilizada para os processos de cálculos.
final double pi = Math.PI;
//Variable utilizada para saber a última vez que chocamos com um parede.
//Aqui temos um instante de tempo
long inCorner;
//Objeto Enemy donde se almacenara el enemigo al que atacaremos.
Enemy target;
//Numero de balas que han impactada en nuestro robot
int hit = 0;
//Entero que nos indica la direccion con la que debemos movernos
int direccion = 1;
//Potencia con la que disparamos
double potencia;
//Hilo principal de ejecución de nuestro robot
public void run() {
//Imponemos un toque de distinción a nuestro tanque
setColors(Color.PINK,Color.ORANGE,Color.YELLOW);
//Inicializamos variables
inCorner=getTime();
target = new Enemy();
//Este valor es inicalizado a este valor para que sea muy grande
target.distance = 900000000;
//Hacemos que los movimientos del radar, cañon y robot sean independientes.
setAdjustGunForRobotTurn(true);
setAdjustRadarForGunTurn(true);
turnRadarRightRadians(2*pi);
while(true) {
//Calcula el próximo movimiento
calculoMovimiento();
//Calcula la potencia de disparo
calculoPotencia();
//Buscamos enemigos
escanear();
//apuntamos al enemigo
apuntar();
//Calculado ya todo, disparamos
fire(potencia);
//Ejecuta todas los resultados de los métodos anteriores.
execute();
}
}
//Calculamos movimiento a realizar
void calculoMovimiento() {
//Si la distancia es mayor a 300, nos acercamos
if(target.distance <300){
//Si ha pasado un tiempo determinado des el ultimo movimiento;
if (getTime()%20 == 0) {
//Establecemos la dirección del movimiento
if(hit<4){
//Cambiamos de direccion;
direccion *= -1;
}
else{
//Si nos impactado 4 veces, describimos un arco de circunferencia durante
//un tiempo determinado
if (getTime()%60 == 0) {
hit = 0;
}
}
//Una vez estableciada la direccion, avanzamos con el robot
setAhead(direccion*(350+(int)((int)Math.random()*350)));
}
//Establecmos el redio de giro respecto al robot
setTurnRightRadians(target.bearing + (pi/2));
}
else{
setAhead(300);
setTurnRightRadians(target.bearing + (pi/4));
}
}
//Método para contar el numero de impactos recibidos
public void onHitByBullet(HitByBulletEvent event) {
hit = hit +1;
//Si el impacto viene de un compañero, nos movemos.
if (isTeammate(event.getName()))
{
hit = 1000;
}
}
//Metodo por si golpeamos una pared
public void onHitWall(HitWallEvent event) {
//Obtenemos el instante de tiempo en que ha ocurrido todo;
long temp = getTime();
//Si ha pasado muy poco tiempo desde el ultimo choque
if ((temp - inCorner) < 100){
//Nos girmos hacia el nemigo y avanzamos hacia él
setBack(100);
setTurnRightRadians(target.bearing);
execute();
setAhead(300);
execute();
}
//Actualizamos la variable para saber la utlima vez que chocamos con la pared
inCorner=temp;
}
//Calculamos el movimiento del radar para apuntar al enemigo
void escanear() {
double radarOffset;
if (getTime() - target.ctime > 5) {
//Si hace mucho que no vemos a nadie, hacemos que mire hacia todas partes.
radarOffset = 360;
}
else {
//Calculamos cuánto se debe mover el radar para apuntar al enemigo
radarOffset = getRadarHeadingRadians() - absbearing(getX(),getY(),target.x,target.y);
//Como hasta que ejecutemos esta función pasará un cierto tiempo,
//añadiremos una pequeña cantidad al offset para no perder al enemigo
if (radarOffset < 0)
radarOffset -= pi/7;
else
radarOffset += pi/7;
}
//Gira el radar
setTurnRadarLeftRadians(NormaliseBearing(radarOffset));
}
//Método para apuntar el cañon
void apuntar() {
//En esta variable, intentamos estimar el tiempo que pasara hasta que nuestra bala llegara al enemigo
long time = getTime() + (int)(target.distance/(20-(3*(400/target.distance))));
//Offset que debemos imponer al cañon
double gunOffset = getGunHeadingRadians() - absbearing(getX(),getY(),target.guessX(time),target.guessY(time));
setTurnGunLeftRadians(NormaliseBearing(gunOffset));
}
//Metodo para obtener un angulo entre pi y -pi
double NormaliseBearing(double ang) {
if (ang > pi)
ang -= 2*pi;
if (ang < -pi)
ang += 2*pi;
return ang;
}
//Metodo para obtener un angulo entre 0 y 2*pi
double NormaliseHeading(double ang) {
if (ang > 2*pi)
ang -= 2*pi;
if (ang < 0)
ang += 2*pi;
return ang;
}
//Devuelve la distancia entre dos puntos
public double distancia( double x1,double y1, double x2,double y2 )
{
return Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
}
//Obtiene el angulo entre dos puntos
public double absbearing( double x1,double y1, double x2,double y2 )
{
double xo = x2-x1;
double yo = y2-y1;
double h = distancia( x1,y1, x2,y2 );
if( xo > 0 && yo > 0 )
{
return Math.asin( xo / h );
}
if( xo > 0 && yo < 0 )
{
return Math.PI - Math.asin( xo / h );
}
if( xo < 0 && yo < 0 )
{
return Math.PI + Math.asin( -xo / h );
}
if( xo < 0 && yo > 0 )
{
return 2.0*Math.PI - Math.asin( -xo / h );
}
return 0;
}
//Método para buscar nuevo enmigo y actualizar la variable target
public void onScannedRobot(ScannedRobotEvent e) {
//Entramos si hemos detectado a nuestro objetivo o a un enemigo
//que se encuentre mas cerca que nosotros
if (isTeammate(e.getName()))
{
return;
}
System.out.println("Escaneo a :" + e.getName());
if ((e.getDistance() < target.distance)||(target.name == e.getName())) {
System.out.println("He entrado");
//Fijamos los paremetros del enemigo
target.name = e.getName();
target.bearing = e.getBearingRadians();
target.head = e.getHeadingRadians();
target.ctime = getTime();
target.speed = e.getVelocity();
target.distance = e.getDistance();
double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*pi);
target.x = getX()+Math.sin(absbearing_rad)*e.getDistance();
target.y = getY()+Math.cos(absbearing_rad)*e.getDistance();
}
}
//Método para calcular la potencia de disparo
void calculoPotencia() {
//Dependiendo la distancia al enemigo, diparo con mas o menos potencia.
//La potencia es inversamente proporcional a la distancia.
potencia = 500/target.distance;
}
//Cuando muere un robot, hago que mi distancia al enemigo sea muy grande.
public void onRobotDeath(RobotDeathEvent e) {
if (e.getName() == target.name)
target.distance = 9000000;
}
//Si nos golpeamos con un robot:
//-Si es compañero nos separamos
//-Si es enemigo, le atacamos
public void onHitRobot(HitRobotEvent event) {
if (isTeammate(event.getName())){
setAhead(direccion*-1*700);
}
else{
if (event.getName() != target.name)
target.distance = 9000000;
}
}
//Gira el cañón en señal de victoria
public void onWin(WinEvent event) {
while(true){
execute();
setTurnGunLeftRadians(pi/2);
}
}
}
//Clases que contiene las características del enemigo
class Enemy {
//Nombre del enemigo
String name;
//Almacena la orientacion
public double bearing;
//Almacena el apuntamiento
public double head;
//Tiempo en que ha sido detectado
public long ctime;
//Velocidad del enemigo
public double speed;
//Posicion del enemigo
public double x,y;
//Distancia del enemigo
public double distance;
//Métodos para obtener y modificar las propiedades
public String getname(){
return name;
}
public double getbearing(){
return bearing;
}
public double gethead(){
return head;
}
public long getctime(){
return ctime;
}
public double getspeed(){
return speed;
}
public double getx(){
return x;
}
public double gety(){
return y;
}
public double getdistance(){
return distance;
}
//Metodos para intentar adivinar la posicion futura del tanque.
public double guessX(long when)
{
long diff = when - ctime;
return x+Math.sin(head)*speed*diff;
}
public double guessY(long when)
{
long diff = when - ctime;
return y+Math.cos(head)*speed*diff;
}
}
