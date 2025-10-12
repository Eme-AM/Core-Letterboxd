export const toCapitalizeCase = (str) => {
    if (!str) return "";
    if (str === 'InQueue') return "In Queue";
    return str
        .split(" ")
        .map(word =>
            word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
        )
        .join(" ");
}
export const formatDateTime = (str) => {
  if (!str) return "";
  return str.replace("T", " ").split(".")[0];
};
