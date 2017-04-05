module.exports = class FolderExplorerView {
  /**
   * @param {Array} toView [{name: test1, isFile: true}] (elementi della cartella che si sta visualizzando)
   * @param {object} up {name: test2} (cartella superiore)
   */
  constructor(toView) {
    var self = this;

    /**
     * toView viene incapsulato e non e direttamente accessibile dall'esterno,
     * si passa sempre dei getter e dai setter
     * 
     * rappresenta gli elementi della cartella che si sta visualizzando
     */
    var _toView = null;
    self.setToView = function(toView) {
      if(!toView) {
        throw new Error("toView e un parametro obbligatorio");
      }
      if(toView.constructor !== Array) {
        throw new TypeError("toView deve essere un array");
      }
      _toView = toView;
    }
    self.getToView = () => _toView;
    self.setToView(toView);
  }

  set toView(toView) {
    this.setToView(toView);
  }

  get toView() {
    return this.getToView();
  }
}