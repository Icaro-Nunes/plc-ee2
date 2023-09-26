import Control.Concurrent
import Control.Concurrent.MVar
import Control.Monad

numMessages :: Int
numMessages = 20

data PingPong = PingPong { pingTurn :: MVar Bool }

-- Função para alternar entre "Ping" e "Pong"
toggle :: Bool -> Bool
toggle True = False
toggle False = True

playPing :: PingPong -> IO ()
playPing game = do
    turn <- takeMVar (pingTurn game)
    when (not turn) $ do
        putStrLn "Ping"
    putMVar (pingTurn game) (toggle turn)

playPong :: PingPong -> IO ()
playPong game = do
    turn <- takeMVar (pingTurn game)
    when turn $ do
        putStrLn "Pong"
    putMVar (pingTurn game) (toggle turn)

playPingPong :: PingPong -> IO ()
playPingPong game = do
    pingThread <- forkIO $ replicateM_ (numMessages+1) (playPing game)
    pongThread <- forkIO $ replicateM_ (numMessages+1) (playPong game)
    threadDelay (numMessages * 1000)
    killThread pingThread
    killThread pongThread

main :: IO ()
main = do
    game <- newMVar True
    playPingPong (PingPong game)