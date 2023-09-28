module Pingping where
import Control.Concurrent
import Control.Monad (replicateM, replicateM_)
import Data.Maybe

popFirst :: [a] -> (Maybe a, [a])
popFirst [] = (Nothing, [])
popFirst l = (Just $ head l, tail l)

-- generator uses MVar as some kind of state about the previous value
counterInc :: MVar Int -> IO Int
counterInc v = do
        val <- takeMVar v
        let inc = val+1
        putMVar v inc
        return val

data PingPing = PingPing {
    queue :: MVar [Int],
    completed :: MVar Bool
}

-- consume function effectively uses the message value
consume :: Int -> IO ()
consume i = do
        putStrLn $ "received "++ show i
        return ()

-- enqueues each value
pingSenderAux :: PingPing -> IO Int -> IO ()
pingSenderAux game numGen = do
        n <- numGen
        q <- takeMVar (queue game)
        putStrLn $ "put "++ show n ++ " on queue"
        putMVar (queue game) (q++[n])

-- invokes all enqueues and then sends completed signal
pingSender :: PingPing -> Int -> IO ()
pingSender game n = do
        counter <- newMVar 0
        replicateM_ n (pingSenderAux game (counterInc counter))
        takeMVar (completed game)
        putMVar (completed game) True


-- receiver consumes queue at each iteration until queue is empty AND done signal is passed
pingReceiverLoop :: PingPing -> IO ()
pingReceiverLoop game = do
        q <- takeMVar (queue game)
        done <- takeMVar (completed game)
        putMVar (completed game) done
        if done && (length q == 0) then do
            putMVar (queue game) q
            return ()
        else do
            let (it, sq) = popFirst q
            if not (isNothing it) then do
                let Just i = it
                consume i
                putMVar (queue game) sq
                pingReceiverLoop game
            else do
                putMVar (queue game) sq
                pingReceiverLoop game


pingReceiver :: PingPing -> IO ()
pingReceiver game = pingReceiverLoop game


main :: IO ()
main = do 
    completed <- newMVar False
    queue <- newMVar []
    let game = PingPing queue completed
    let n = 30
    senderThread <- forkIO $ (pingSender game n)
    threadDelay (300)
    receiverThread <- forkIO $ (pingReceiver game)
    threadDelay (n * 50 * 1000)
    killThread senderThread
    killThread receiverThread
    




