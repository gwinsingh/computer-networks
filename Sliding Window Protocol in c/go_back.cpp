#include<bits/stdc++.h>

using namespace std;


class sender{
    private:
    int n;
    int m;
    vector<bool>b;
    vector<int> a;

    public:

    int get_size(){
    return m;}

    int get_window_size(){
    return n;}

    sender()
    {
         m=100;
        for(int i=0;i<m;i++)
        {
            a.push_back(rand()%100);
        }

        n=rand()%20;

       /* for(int i=0;i<m;i++)
        {
            b.push_back(rand()%2);
        }*/

    }

    vector<int> send(int neg_ack)
    {
        vector<int> v;
        for(int i=neg_ack;i<min(neg_ack+n,m);i++)
        {
            bool error=rand()%2;
            if(error)
            v.push_back(-1);
            else
            v.push_back(a[i]);
        }

        cout<<"sending "<<neg_ack<<" to "<<(min(neg_ack+n,m))<<endl;

        return v;
    }


};


class reciever{
    private:
    int n;
    int m;
    vector<int> a;

    public:
    void set_size(int m){
    this->m=m;}

    void set_window_size(int n){
    this->n=n;}


    int recieve(vector<int> v )
    {

        int sz=v.size();
        for(int i=0;i<sz;i++)
        {
            if(v[i]==-1)
            {
                cout<<"error in recieving "<<a.size()<<endl;
                return a.size();
            }

            else
            {
                a.push_back(v[i]);
            }
        }

                cout<<"recieved all packets from this window "<<endl;
        return a.size();

    }

    bool check_full()
    {
        if(a.size()==m)
        return 1;
        return 0;
    }

};



int main()
{
    sender s;
    reciever r;
    r.set_size(s.get_size());
    r.set_window_size(s.get_window_size());

    int neg_ack=0;
    while(!r.check_full())
    {
        neg_ack=r.recieve(s.send(neg_ack));
    }

    cout<<"All packets recieved successfully "<<endl;

   // cout<<s.get_size()<<" "<<s.get_window_size()<<endl;

}
