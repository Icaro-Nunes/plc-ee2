module Pingping where
import Control.Concurrent (MVar, takeMVar, putMVar)
import Control.Monad (replicateM)

popFirst :: [a] -> (Maybe a, [a])
popFirst [] = (Nothing, [])
popFirst (a: ls) = (Just a, ls)

data PingPing = PingPing {
    queue :: MVar [Int],
    completed :: MVar Bool
}

consume :: Int -> IO ()
consume i = putStrLn $ show i

pingSenderAux :: PingPing -> Int -> IO ()
pingSenderAux game n = do
        q <- takeMVar (queue game)
        putMVar (queue game) (q++[n])

pingSender :: PingPing -> Int -> IO()
pingSender game n = replicateM n (pingSenderAux game n)



main :: IO ()
main = do
    undefined

