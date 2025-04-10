import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

df = pd.read_csv("results.csv",  sep=',')
win_rate = df.groupby('CURRENT_GEN')['win'].mean().reset_index()

sns.lineplot(data=df, x='CURRENT_GEN', y='win', ci='sd')
plt.title("Win Rate moyen par Génération")
plt.ylabel("Win Rate")
plt.xlabel("Generation")
plt.show()

sns.boxplot(data=df, x='CURRENT_GEN', y='moves')
plt.title("Moves Distribution per Generation")
plt.ylabel("Moves")
plt.xlabel("Generation")
plt.show()

df['capture_diff'] = df['white_captures'] - df['black_captures']

sns.boxplot(data=df, x='CURRENT_GEN', y='capture_diff')
plt.title("Capture Advantage per Generation (White - Black)")
plt.ylabel("Capture Difference")
plt.xlabel("Generation")
plt.axhline(0, color='gray', linestyle='--')
plt.show()


weights_df = df['weights_str'].str.split(';', expand=True).astype(float)
weights_df.columns = [f'W{i+1}' for i in weights_df.columns]

df = pd.concat([df, weights_df], axis=1)


for col in weights_df.columns:
    df[col] = weights_df[col]

weights_long = df.melt(id_vars='CURRENT_GEN', value_vars=weights_df.columns,
                       var_name='Weight', value_name='Value')

sns.lineplot(data=weights_long, x='CURRENT_GEN', y='Value', hue='Weight')
plt.title("Evolution des poids par génération")
plt.show()

correlation_df = df[weights_df.columns.tolist() + ['win']].corr()
sns.heatmap(correlation_df, annot=True, cmap='coolwarm', center=0)
plt.title("Correlation Between Weights and Win Rate")
plt.show()