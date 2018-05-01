from setuptools import setup, find_packages

setup(
    name="KMeans Evaluation",
    version="0.1",
    python_requires='>=3.6.0',
    packages=find_packages(),

    # Project uses reStructuredText, so ensure that the docutils get
    # installed or upgraded on the target machine
    install_requires=[
        "numpy>=1.3.0",
        "scipy>=0.19.0",
        "gensim>=3.4.0",
        "scikit-learn>=0.18.1",
        "matplotlib>=2.0.1",
    ],

    setup_requires=[
        "numpy>=1.12.0",
        "scipy>=0.19.0",
        "gensim>=3.4.0",
        "scikit-learn>=0.18.1",
        "matplotlib>=2.0.1",
    ],

    entry_points={
        'console_scripts': [
            "evaluate=KMeansClusterer.kmeans:main",
        ],
    },

    author=[
        "Adam Bowers"
    ],
    license="MIT",
    # could also include long_description, download_url, classifiers, etc.
)
