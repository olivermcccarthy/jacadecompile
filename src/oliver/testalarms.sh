#!/bin/bash
if [ ! -f $1 ]
then
   exit -1
fi
.  $1

EXPORT=`bluemix cs cluster-config $CLUSTERNAME | grep export`

  $EXPORT

function exitAndResult() {
  
  echo TEST_RESULT=$2 >> RESULT
  exit $1
 
  
}

echo TEST_RESULT=FAILED > RESULT
RESTIN=`kubectl get service | grep proxy  | sed -E "s/ +/~/g"  | cut -d '~' -f3`
if [ -z $RESTIN ]
then
   exitAndResult -1 "could not find restin service"

fi
RESTOUT=`kubectl get service | grep ioadrs  | sed -E "s/ +/~/g"  | cut -d '~' -f3`
if [ -z $RESTOUT ]
then
   
    exitAndResult -1 "could not find restout service" 
fi


echo REST_OUT_IP $RESTOUT REST_IN_IP $RESTIN

cd Kube/ioametadata


if [ ! -f testdata/$TEST_FILE ]
then
 echo testdata/$TEST_FILE does not exist
   usage
    exit -1
fi
set +x

cp testdata/$TEST_FILE  testdata/$TEST_FILE.bak
sed -i -E "s/(\"resourceID\".\")[^\"]+/\1$RESOURCE_ID/" testdata/$TEST_FILE.bak
sed -i -E "s/(\"node\".\")[^\"]+/\1$RESOURCE_ID/"  testdata/$TEST_FILE.bak

echo curl -vX POST $RESTIN/api/metrics
sleep 20
curl -vX POST $RESTIN/api/metrics -d @testdata/$TEST_FILE.bak --header "Content-Type: application/json" --header "tenantID: $TENANT_ID"  --header "oaApiKey: 64307def-7f1e-3682-bb73-c5c9565f9d69"
COUNT_METRIC=0
COUNT_BASELINE=0
echo TESTING $TENANT_ID
while [ $COUNT_METRIC -lt 1 ] && [ $COUNT_BASELINE -lt 1 ]
do

    echo  ATTEMPTS $ATTEMPTS
    ATTEMPTS=`expr $ATTEMPTS + 1`
    if [ $ATTEMPTS -gt 10 ]
    then
         exitAndResult -1  "Tried too often for baselines" 
       
    fi
        COUNT_METRIC=0
        COUNT_BASELINE=0
        echo sleep 20
        sleep 20
        curl  $RESTOUT/api/metadata  --header "Content-Type: application/json" --header "tenantID: $TENANT_ID"  --header "oaApiKey: 8c545d86-7d57-3f7d-8f58-88b501869061"  > RES
        cat RES
        echo got result
        for mr_id in `cat RES | sed -E "s/}/\n/g" | grep $RESOURCE_ID| sed "s/.*:"//`
        do
          echo checking $mr_id 
           curl $RESTOUT/api/metrics?id=$mr_id&startTime=1472089500000&endTime=1472099500000  --header "Content-Type: application/json" --header "tenantID: $TENANT_ID"  --header "oaApiKey: 8c545d86-7d57-3f7d-8f58-88b501869061" > RES
          cat RES
          COUNT_VALUES=`cat RES | sed "s/}/\n/g" | wc -l`
          cat RES | sed "s/}/\n/g" | tail -10
          if [ $COUNT_VALUES -gt 0 ]
          then
              COUNT_METRIC=`expr $COUNT_METRIC + 1`
          fi

            

           curl  $RESTOUT/api/baselines?id=$mr_id&startTime=1472089500000&endTime=1472099500000  --header "Content-Type: application/json" --header "tenantID: $TENANT_ID"  --header "oaApiKey: 8c545d86-7d57-3f7d-8f58-88b501869061" > RES
          COUNT_VALUES=`cat RES | sed "s/}/\n/g" | wc -l`
           BASELINES_SEEN=`cat RES | sed "s/}/\n/g" | tail -10`
          echo BASELINES_SEEN="$BASELINES_SEEN" >> TEST_RESULT 
          if [ $COUNT_VALUES -gt 0 ]
          then
              COUNT_BASELINE=`expr $COUNT_BASELINE + 1`
          fi
        done
    echo COUNT METRIC got $COUNT_METRIC 
    echo COUNT_BASELINE $COUNT_BASELINE 
done

COUNT_ALARM=0

echo TESTING $TENANT_ID
while [ $COUNT_ALARM -lt 1 ] 
do

    echo  ATTEMPTS $ATTEMPTS
    ATTEMPTS=`expr $ATTEMPTS + 1`
     if [ $ATTEMPTS -gt 20 ]
    then
         exitAndResult -1  "Tried too often for alarms" 
       
    fi
 curl  "$RESTOUT/api/anomalies?startTime=1472089500000&endTime=1472300400000"  --header "Content-Type: application/json" --header "tenantID: $TENANT_ID" --header "oaApiKey: 8c545d86-7d57-3f7d-8f58-88b501869061" > ALARMS
 cat ALARMS
 
  sleep 60

         COUNT_VALUES=`cat ALARMS | grep "is now a flat" | wc -l` 
          
          if [ $COUNT_VALUES -gt 0 ]
          then
          
            echo ALARMS_SEEN=`cat ALARMS` >> RESULT
          
             COUNT_ALARM=2
          fi
          
        
done

exitAndResult 0 Success