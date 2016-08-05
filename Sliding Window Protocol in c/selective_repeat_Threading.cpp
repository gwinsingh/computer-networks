#include<iostream>
#include<pthread.h>
#include<semaphore.h>
#include<stdlib.h>
#include<time.h>
#include<unistd.h>
using namespace std;
pthread_t sender,receiver;
const int LIMIT=1000;
const int total_packets_on_receiver_side=50;
int sender_packet[LIMIT];
int receiver_packet[LIMIT];
int sender_count=0,receiver_count=0;
pthread_mutex_t mutex=PTHREAD_MUTEX_INITIALIZER;
int timeout=0;
const int PROBABILITY_COUNT=10;
int probability[PROBABILITY_COUNT];
int total_packet_sent=0;
time_t start,end;
int arg=0;
int packet_loss=0;
int receiver_buffer_count=0;
void initialize()
{
    probability[0]=2;
    for(int i=1;i<PROBABILITY_COUNT;++i)
    {
        probability[i]=(probability[i-1]*4);
        cout<<probability[i]<<" ";
    }
    cout<<"\n\n";
    for(int i=0;i<LIMIT;++i)
    {
        sender_packet[i]=0;
        receiver_packet[i]=0;
    }
}

bool isLost(int a)
{
    for(int i=0;i<PROBABILITY_COUNT;++i)
    {
        if(a==probability[i])
        {
            probability[i]=-1;
            return true;
        }
    }
    return false;
}
void* sender_function(void* arg)
{
    cout<<"sender function\n";

    while(1)
    {

        pthread_mutex_lock(&mutex);
        cout<<"packet "<<sender_count<<" send   - - ->\n";
        total_packet_sent++;
        sender_packet[sender_count]=1;
        pthread_mutex_unlock(&mutex);
        usleep(1000000);
        if(!isLost(sender_count))
        {

            while(receiver_packet[++sender_count]!=0);

        }
        else
        {
            cout<<"packet "<<sender_count+1<<" send but *LOST*"<<"\n";
            sender_packet[sender_count+1]=0;
            sender_count=sender_count+2;

        }


    }


}

void* receiver_function(void* arg)
{
    cout<<"receiver function\n";
    int previous=-1;
    int counter=0;
    while(counter++!=total_packets_on_receiver_side)
    {
        //sem_wait(&scount);
        usleep(1200000);
        if(sender_packet[receiver_count]==1)
        {
            receiver_packet[receiver_count]=1;
            cout<<"                     received "<<receiver_count<<"\n";
            receiver_count++;
            //receiver_buffer_count++;
        }
        else
        {
            receiver_buffer_count=receiver_count;
            cout<<"NEGATIVE ACKNOWLEDGEMENT SENT for "<<receiver_count<<"\n";
            while(sender_packet[++receiver_buffer_count]==1)
            {
                receiver_packet[receiver_buffer_count]=1;
                cout<<"BUFFERING "<<receiver_buffer_count<<" packet\n";
            }

            cout<<"\n\nTIMEOUT\n going to "<<receiver_count<<" packet\n\n";
            packet_loss++;
            pthread_mutex_lock(&mutex);
            sender_count=receiver_count-1;
            pthread_mutex_unlock(&mutex);
        }
    }
}


int main()
{
    initialize();
    //sem_init(&scount,0,-1);
	//sem_init(&rcount,0,size);
	time(&start);
    pthread_create(&sender,NULL,sender_function,NULL);
    pthread_create(&receiver,NULL,receiver_function,NULL);
    //pthread_join(sender,NULL);
    pthread_join(receiver,NULL);
    time(&end);
    cout<<"Total packet sent ="<<receiver_count+packet_loss-1<<"\n";
    cout<<"total packet received ="<<receiver_count-1<<"\n";
    cout<<"total time taken = "<<end-start<<"\n";
    cout<<"average time taken for "<<receiver_count-1<<" packets to send "<<(end-start)<<" seconds\n";
    cout<<"packet lost by Network ="<<packet_loss<<"\n";
    cout<<"percentage of packet lost in NETWORK ="<<(100*((float)packet_loss/(receiver_count-1)))<<"\n";
    cout<<"average packet delay "<<(float)(end-start)/(receiver_count-1)<<" seconds\n";
    return 0;
}




