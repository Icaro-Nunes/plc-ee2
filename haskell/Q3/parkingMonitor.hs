import Data.Map
import Control.Monad (replicateM_, replicateM)
import Control.Concurrent (newMVar, threadDelay, forkIO, killThread, ThreadId)
import GHC.MVar
import Data.Time (getCurrentTime, UTCTime, diffUTCTime)
import Data.Semigroup (diff)

type ParkingLot = Map Int Bool

data MonitorInfo = MonitorInfo {
    isPcdCar :: Int -> Bool,
    isPcdVacancy :: Int -> Bool,
    parkingLot :: MVar ParkingLot,
    originTime :: UTCTime
}

unpackMonitor :: MonitorInfo -> (Int -> Bool, Int -> Bool, MVar ParkingLot, UTCTime)
unpackMonitor monitor = (isPcdCar monitor, isPcdVacancy monitor, parkingLot monitor, originTime monitor)

isPcdVacancyGenerator :: Int -> Int -> Bool
isPcdVacancyGenerator numVac index = index <= (numVac `div` 10)

isPcdCarGenerator :: Int -> Int -> Bool
isPcdCarGenerator numCars index = index <= (numCars `div` 5)

initParkingLot :: ParkingLot -> Int -> ParkingLot
initParkingLot m 0 = insert 0 False m
initParkingLot m n = initParkingLot (insert n False m) (n-1)

isAvailableVacancy :: Bool -> Bool -> Bool -> Bool
isAvailableVacancy occupied isPcdCar isPcdVacancy = not occupied && (isPcdCar || not isPcdVacancy)

occupyIfVacant :: ParkingLot -> (Int -> Bool) -> Bool -> (Int, ParkingLot)
occupyIfVacant parkingLot isPcdVacancy isPcdCar =
        if Prelude.null availableList then (-1, parkingLot)
        else (head availableList, insert (head availableList) True parkingLot)
    where availableList = [fst i | i <- toList parkingLot, isAvailableVacancy (snd i) isPcdCar (isPcdVacancy (fst i))]

carLoop :: MonitorInfo -> Int -> IO ()
carLoop monitor id = do
    let (isPcdCar, isPcdVacancy, parkingLot, originTime) = unpackMonitor monitor
    let isPcd = isPcdCar id
    pl <- takeMVar parkingLot
    let (placeOccupied, newPl) = occupyIfVacant pl isPcdVacancy isPcd
    currentTime <- getCurrentTime
    let ocDiff = diffUTCTime currentTime originTime
    if placeOccupied /= -1 then do
        putStrLn ("(occupied) car " ++ show id ++ " vacancy " ++ show placeOccupied ++ " at time " ++ show ocDiff)
        putMVar parkingLot newPl
        threadDelay ((if isPcd then 1500 else 1000)*1000)
        restorePl <- takeMVar parkingLot
        deocTime <- getCurrentTime
        let deocDiff = diffUTCTime deocTime originTime
        putStrLn ("(freed) car " ++ show id ++ " vacancy " ++ show placeOccupied ++ " at time " ++ show deocDiff)
        putMVar parkingLot (insert placeOccupied False restorePl)
        return ()
    else do
        putStrLn ("(hold) car "++show id++ " at time "++show ocDiff)
        putMVar parkingLot newPl
        threadDelay 100
        carLoop monitor id
    return ()

initCar :: MonitorInfo -> IO (Int, Int) -> IO ()
initCar monitor gen = do
    (id, startTimeMs) <- gen
    threadDelay (startTimeMs * 1000)
    carLoop monitor id

carInfoGenerator :: MVar Int -> IO (Int, Int)
carInfoGenerator n = do
    index <- takeMVar n
    putMVar n (index+1)
    return (index, index*200)

parkingMonitor :: Int -> Int -> IO ()
parkingMonitor k n = do
    let pl = initParkingLot empty k
    putStrLn (" parkingLot: "++show (toList pl))
    parkingLot <- newMVar pl
    let isPcdVacancy = isPcdVacancyGenerator k
    let isPcdCar = isPcdCarGenerator n
    originTime <- getCurrentTime
    let monitor = MonitorInfo isPcdCar isPcdVacancy parkingLot originTime
    s <- newMVar 0
    threads <- replicateM n (forkIO $ initCar monitor (carInfoGenerator s))
    threadDelay (n* 1500 * 1000)
    killThreads threads
    return ()

killThreads :: [ThreadId] -> IO ()
killThreads [] = return ()
killThreads (t: sl) = do
    killThread t
    killThreads sl

main :: IO ()
main = do
    let n = 30
    parkingMonitor 5 n
