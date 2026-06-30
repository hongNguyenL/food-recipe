import logging


def setup_logger(log_file: str = "importer.log",
                 level: str = "INFO") -> logging.Logger:
    logger = logging.getLogger("foodrecipe_importer")
    logger.setLevel(getattr(logging, level.upper(), logging.INFO))
    logger.handlers.clear()

    formatter = logging.Formatter(
        "%(asctime)s  %(levelname)-8s  %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )

    handler = logging.FileHandler(log_file, encoding="utf-8")
    handler.setFormatter(formatter)
    logger.addHandler(handler)

    return logger
