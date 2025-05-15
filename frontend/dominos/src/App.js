const Pizza = (props) => {
  return React.createElement("div", {}, [
    React.createElement("h1", {}, props.name),
    React.createElement("p", {}, props.description),
  ]);
};

const App = () => {
  return React.createElement("div", {}, [
    React.createElement("h1", {}, "Domino's"),
    React.createElement(Pizza, {
      name: "The Pepperoni Pizza",
      description: "some dope pepperoni",
    }),
    React.createElement(Pizza, {
      name: "The Hawain",
      description: "pineapple and ham, tf",
    }),
    React.createElement(Pizza, {
      name: "Americano Pizza",
      description: "French fries and hot dogs",
    }),
    React.createElement(Pizza, {
      name: "BBQ Chicken Pizza",
      description: "chicken nuggets on your pizza glazed with bbq sauce",
    }),
    React.createElement(Pizza, {
      name: "Meatzza",
      description: "selective beef on pizza",
    }),
  ]);
};

const container = document.getElementById("root");
const root = ReactDOM.createRoot(container);
root.render(React.createElement(App));
