import pandas as pd


def read_csv(filepath: str, chunksize: int = 1000):
    for chunk in pd.read_csv(filepath, chunksize=chunksize, dtype=str):
        for _, row in chunk.iterrows():
            yield row.to_dict()
