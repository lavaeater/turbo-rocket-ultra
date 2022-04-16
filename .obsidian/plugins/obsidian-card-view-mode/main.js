'use strict';

var obsidian = require('obsidian');

/*! *****************************************************************************
Copyright (c) Microsoft Corporation.

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
***************************************************************************** */
/* global Reflect, Promise */

var extendStatics = function(d, b) {
    extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
    return extendStatics(d, b);
};

function __extends(d, b) {
    if (typeof b !== "function" && b !== null)
        throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
    extendStatics(d, b);
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
}

function __awaiter(thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
}

function __generator(thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
}

var DEFAULT_SETTINGS = {
    disabled: true,
    cardTitle: false,
    cardCornerRadius: 10,
    cardTitleCornerRadius: 5,
    cardDropShadow: true,
    removePaneBoundaries: true,
    hideScrollBar: true,
    colorBackGroundLight: "255, 255, 255",
    colorBackGroundDark: "145, 145, 145",
    colorActiveCardLight: "255, 255, 255",
    colorActiveCardDark: "71, 71, 71",
    colorNonActiveCardDark: "71, 71, 71",
    colorNonActiveCardLight: "255, 255, 255",
    colorTitleCardEdgeDark: "227, 76, 38",
    colorTitleCardEdgeLight: "123, 230, 1",
    colorTitleCardBackGroundLight: "242, 242, 242",
    colorTitleCardBackGroundDark: "0, 0, 0",
    colorDiffBetweenActive: 0,
    colorCardBorderLight: "255, 255, 255",
    colorCardBorderDark: "0, 0, 0"
};
var CardViewModeSettingTab = /** @class */ (function (_super) {
    __extends(CardViewModeSettingTab, _super);
    function CardViewModeSettingTab(app, plugin) {
        var _this = _super.call(this, app, plugin) || this;
        _this.plugin = plugin;
        return _this;
    }
    CardViewModeSettingTab.prototype.display = function () {
        this.containerEl.empty();
        this.containerEl.createEl("h3", {
            text: "General Settings",
        });
        this.addSettingToggleCardView();
        this.addSettingToggleCardTitle();
        this.containerEl.createEl("h3", {
            text: "Card View Designer",
        });
        this.addSettingToggleRemovePaneBoundariies();
        this.addSettingHideScrollbar();
        this.addSettingToggleDropShadow();
        this.addSettingCardCornerRadius();
        this.addSettingCardTitleCornerRadius();
        this.containerEl.createEl("h4", {
            text: "Dark Mode Color",
        });
        this.addSettingBackgroundColorDark();
        this.addSettingActiveCardColorDark();
        this.addSettingCardBorderColorDark();
        this.addSettingCardTitleBackgroundColorDark();
        this.addSettingCardTitleEdgeColorDark();
        // detail summary
        // this.containerEl.createEl("details", {
        //   text: ""
        // }).createEl("summary", {
        //   text: "Dark mode",
        // });
        this.containerEl.createEl("h4", {
            text: "Light Mode Color"
        });
        this.addSettingBackgroundColorLight();
        this.addSettingActiveCardColorLight();
        this.addSettingCardBorderColorLight();
        this.addSettingCardTitleBackgroundColorLight();
        this.addSettingCardTitleEdgeColorLight();
        this.containerEl.createEl("h3", {
            text: "Attention Pane",
        });
        this.addSettingDiffBetActiveNonactive();
    };
    CardViewModeSettingTab.prototype.addSettingToggleCardView = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Toggle Card View")
            .setDesc("Turns card view mode on or off globally.")
            .addToggle(function (toggle) { return toggle.setValue(!_this.plugin.settings.disabled)
            .onChange(function (value) {
            _this.plugin.settings.disabled = !value;
            _this.plugin.saveData(_this.plugin.settings);
            if (_this.plugin.settings.disabled) {
                _this.plugin.disable();
            }
            else {
                _this.plugin.enable();
            }
        }); });
    };
    CardViewModeSettingTab.prototype.addSettingToggleCardTitle = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Toggle Title Card")
            .setDesc("View note titles as cards. Enable this option with Embedded Note Title Plugin.")
            .addToggle(function (toggle) { return toggle.setValue(_this.plugin.settings.cardTitle)
            .onChange(function (value) {
            _this.plugin.settings.cardTitle = value;
            _this.plugin.saveData(_this.plugin.settings);
            _this.plugin.refresh();
        }); });
    };
    CardViewModeSettingTab.prototype.addSettingToggleRemovePaneBoundariies = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Remove Pane Boundaries")
            .setDesc("When toggled, pane boundaries will be removed.")
            .addToggle(function (toggle) { return toggle.setValue(_this.plugin.settings.removePaneBoundaries)
            .onChange(function (value) {
            _this.plugin.settings.removePaneBoundaries = value;
            _this.plugin.saveData(_this.plugin.settings);
            _this.plugin.refresh();
        }); });
    };
    CardViewModeSettingTab.prototype.addSettingHideScrollbar = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Hide scrollbar")
            .setDesc("When toggled, scrollbar will be removed.")
            .addToggle(function (toggle) { return toggle.setValue(_this.plugin.settings.hideScrollBar)
            .onChange(function (value) {
            _this.plugin.settings.hideScrollBar = value;
            _this.plugin.saveData(_this.plugin.settings);
            _this.plugin.refresh();
        }); });
    };
    CardViewModeSettingTab.prototype.addSettingToggleDropShadow = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Drop Shadow")
            .setDesc("When toggled, dropping shadow to cards will be activated.")
            .addToggle(function (toggle) { return toggle.setValue(_this.plugin.settings.cardDropShadow)
            .onChange(function (value) {
            _this.plugin.settings.cardDropShadow = value;
            _this.plugin.saveData(_this.plugin.settings);
            _this.plugin.refresh();
        }); });
    };
    CardViewModeSettingTab.prototype.addSettingCardCornerRadius = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName('Card Corner Radius')
            .setDesc('Set number to adjust card corner radius. Default radius is 10px.')
            .addText(function (text) { return text.setPlaceholder('Default: 10px')
            .setValue((_this.plugin.settings.cardCornerRadius || '') + '')
            .onChange(function (value) {
            var nu = Number(value);
            _this.plugin.settings.cardCornerRadius = nu;
            _this.plugin.saveData(_this.plugin.settings);
            _this.plugin.refresh();
        }); });
    };
    CardViewModeSettingTab.prototype.addSettingCardTitleCornerRadius = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName('Title Card Corner Radius')
            .setDesc('Set number to adjust title card corner radius. Default radius is 5px.')
            .addText(function (text) { return text.setPlaceholder('Default: 5px')
            .setValue((_this.plugin.settings.cardTitleCornerRadius || '') + '')
            .onChange(function (value) {
            var nu = Number(value);
            _this.plugin.settings.cardTitleCornerRadius = nu;
            _this.plugin.saveData(_this.plugin.settings);
            _this.plugin.refresh();
        }); });
    };
    CardViewModeSettingTab.prototype.addSettingCardBorderColorLight = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Card Border Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorCardBorderLight)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorCardBorderLight);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorCardBorderLight = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingCardBorderColorDark = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Card Border Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorCardBorderDark)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorCardBorderDark);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorCardBorderDark = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingBackgroundColorLight = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Background Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorBackGroundLight)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorBackGroundLight);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorBackGroundLight = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingBackgroundColorDark = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Background Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorBackGroundDark)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorBackGroundDark);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorBackGroundDark = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingActiveCardColorLight = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Active Card Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorActiveCardLight)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorActiveCardLight);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorActiveCardLight = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.settings.colorNonActiveCardLight = color.r - _this.plugin.settings.colorDiffBetweenActive + ", " + (color.g - _this.plugin.settings.colorDiffBetweenActive) + ", " + (color.b - _this.plugin.settings.colorDiffBetweenActive);
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingActiveCardColorDark = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Active Card Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorActiveCardDark)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorActiveCardDark);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorActiveCardDark = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.settings.colorNonActiveCardDark = color.r - _this.plugin.settings.colorDiffBetweenActive + ", " + (color.g - _this.plugin.settings.colorDiffBetweenActive) + ", " + (color.b - _this.plugin.settings.colorDiffBetweenActive);
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingCardTitleEdgeColorDark = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Title Card Edge Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorTitleCardEdgeDark)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorTitleCardEdgeDark);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorTitleCardEdgeDark = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingCardTitleEdgeColorLight = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Title Card Edge Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorTitleCardEdgeLight)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorTitleCardEdgeLight);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorTitleCardEdgeLight = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingCardTitleBackgroundColorLight = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Title Card Background Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorTitleCardBackGroundLight)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorTitleCardBackGroundLight);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorTitleCardBackGroundLight = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    CardViewModeSettingTab.prototype.addSettingCardTitleBackgroundColorDark = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName("Title Card Background Color")
            .controlEl.createEl("input", {
            type: "color",
            value: rgbToHex(this.plugin.settings.colorTitleCardBackGroundDark)
        }, function (el) {
            el.value = rgbToHex(_this.plugin.settings.colorTitleCardBackGroundDark);
            el.oninput = function (_a) {
                var target = _a.target;
                var color = hexToRgb(target.value);
                if (!color)
                    return;
                _this.plugin.settings.colorTitleCardBackGroundDark = color.r + ", " + color.g + ", " + color.b;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            };
        });
    };
    // SLIDER setting
    // new Setting(containerEl)
    //   .setName('Diff Between Active & NonActive Cards')
    //   .setDesc('Spcifiy Color difference between active & non active cards. Set 0 to diable "Attention pane"')
    //   .addSlider(slider => slider
    //     .setLimits(100, 100, 5)
    //     .setValue(this.plugin.settings.colorDiffBetweenActive)
    //     .onChange((value) => {
    //       this.plugin.settings.colorDiffBetweenActive = value;
    //       let activeColorLight = hexToRgb(rgbToHex(this.plugin.settings.colorActiveCardDark));
    //       let activeColorDark = hexToRgb(rgbToHex(this.plugin.settings.colorActiveCardDark));
    //       this.plugin.settings.colorNonActiveCardLight = `${activeColorLight.r - value}, ${activeColorLight.g - value}, ${activeColorLight.b - value}`;
    //       this.plugin.settings.colorNonActiveCardDark = `${activeColorDark.r - value}, ${activeColorDark.g - value}, ${activeColorDark.b - value}`;
    //       this.plugin.saveData(this.plugin.settings);
    //       this.plugin.refresh();
    //     })
    // );
    CardViewModeSettingTab.prototype.addSettingDiffBetActiveNonactive = function () {
        var _this = this;
        new obsidian.Setting(this.containerEl)
            .setName('Diff Between Active & NonActive Cards')
            .setDesc('Set Color difference between active & non active cards. Set this value 0 to diable "Attention pane". Value range: "-255~255".')
            .addText(function (text) { return text.setPlaceholder('Default: 0')
            .setValue((_this.plugin.settings.colorDiffBetweenActive || '') + '')
            .onChange(function (value) {
            var nu = Number(value);
            _this.plugin.settings.colorDiffBetweenActive = nu;
            var activeColorLight = hexToRgb(rgbToHex(_this.plugin.settings.colorActiveCardLight));
            var activeColorDark = hexToRgb(rgbToHex(_this.plugin.settings.colorActiveCardDark));
            _this.plugin.settings.colorNonActiveCardLight = activeColorLight.r - nu + ", " + (activeColorLight.g - nu) + ", " + (activeColorLight.b - nu);
            _this.plugin.settings.colorNonActiveCardDark = activeColorDark.r - nu + ", " + (activeColorDark.g - nu) + ", " + (activeColorDark.b - nu);
            _this.plugin.saveData(_this.plugin.settings);
            _this.plugin.refresh();
        }); });
    };
    return CardViewModeSettingTab;
}(obsidian.PluginSettingTab));
var CardViewModeCommands = /** @class */ (function () {
    function CardViewModeCommands(plugin) {
        this.plugin = plugin;
    }
    CardViewModeCommands.prototype.addToggleSettingCommand = function (id, name, settingName) {
        var _this = this;
        this.plugin.addCommand({
            id: id,
            name: name,
            callback: function () {
                // switch the setting, save and refresh
                //@ts-ignore
                _this.plugin.settings[settingName] = !_this.plugin.settings[settingName];
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.refresh();
            }
        });
    };
    CardViewModeCommands.prototype.addCommands = function () {
        var _this = this;
        this.plugin.addCommand({
            id: 'toggle-card-view-mode',
            name: 'Toggle Card View',
            callback: function () {
                // switch the disabled setting and save
                _this.plugin.settings.disabled = !_this.plugin.settings.disabled;
                _this.plugin.saveData(_this.plugin.settings);
                _this.plugin.settings.disabled ? _this.plugin.disable() : _this.plugin.enable();
            }
        });
        this.addToggleSettingCommand('toggle-card-title', 'Toggle Title Card', 'cardTitle');
    };
    return CardViewModeCommands;
}());
function hexToRgb(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result
        ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16)
        }
        : null;
}
function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}
function rgbToHex(rgb) {
    var result = /^(\d+),\s?(\d+),\s?(\d+)/i.exec(rgb);
    if (!result || !result.length) {
        return "";
    }
    return "#" + componentToHex(Number(result[1])) + componentToHex(Number(result[2])) + componentToHex(Number(result[3]));
}

var CardViewModePlugin = /** @class */ (function (_super) {
    __extends(CardViewModePlugin, _super);
    function CardViewModePlugin() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.style = document.head.createEl('style');
        _this.enable = function () {
            _this.app.workspace.onLayoutReady(_this.reallyEnable);
        };
        _this.reallyEnable = function () {
            _this.addStyle();
        };
        _this.disable = function () {
            _this.removeStyle();
        };
        _this.refresh = function () {
            _this.updateStyle();
        };
        _this.removeStyle = function () {
            var el = _this.style;
            if (el)
                el.remove();
            document.body.classList.remove('plugin-card-view-mode');
            document.body.classList.remove('plugin-card-view-mode-cardtitle');
            document.body.classList.remove('plugin-card-view-mode-dropshadow');
            document.body.classList.remove('plugin-card-view-mode-remove-pane-boudaries');
            document.body.classList.remove('plugin-card-view-mode-hide-scrollbar');
        };
        _this.addStyle = function () {
            _this.style.setAttribute('type', 'text/css');
            // add style in head tag
            document.getElementsByTagName("head")[0].appendChild(_this.style);
            document.body.classList.add('plugin-card-view-mode');
            _this.updateStyle();
        };
        _this.updateStyle = function () {
            // a. update boolean settings
            document.body.classList.toggle('plugin-card-view-mode-cardtitle', _this.settings.cardTitle);
            document.body.classList.toggle('plugin-card-view-mode-dropshadow', _this.settings.cardDropShadow);
            document.body.classList.toggle('plugin-card-view-mode-remove-pane-boudaries', _this.settings.removePaneBoundaries);
            document.body.classList.toggle('plugin-card-view-mode-hide-scrollbar', _this.settings.hideScrollBar);
            // b. update custom css properties
            var el = _this.style;
            if (!el)
                throw "plugin-card-view-mode element not found!";
            else {
                el.textContent = "\n        body.plugin-card-view-mode {\n          --cardview-card-boarder-radius: " + _this.settings.cardCornerRadius + "px;\n          --cardview-embedded-title-boarder-radius: " + _this.settings.cardTitleCornerRadius + "px;\n        }\n        body.plugin-card-view-mode.theme-light{\n          --cardview-card-color-active: rgb(" + _this.settings.colorActiveCardLight + ");\n          --cardview-card-color-non-active: rgb(" + _this.settings.colorNonActiveCardLight + ");\n          --cardview-background-color-default: rgb(" + _this.settings.colorBackGroundLight + ");\n          --header-color-background: rgb(" + _this.settings.colorTitleCardBackGroundLight + ");\n          --cardview-border-color: rgb(" + _this.settings.colorCardBorderLight + ");\n          --cardview-embedded-title-border-right-color-edit: rgb(" + _this.settings.colorTitleCardEdgeLight + ");\n          --cardview-embedded-title-border-right-color-preview: rgb(" + _this.settings.colorTitleCardEdgeLight + ");\n        }\n        body.plugin-card-view-mode.theme-dark{\n          --cardview-card-color-active: rgb(" + _this.settings.colorActiveCardDark + ");\n          --cardview-card-color-non-active: rgb(" + _this.settings.colorNonActiveCardDark + ");\n          --cardview-background-color-default: rgb(" + _this.settings.colorBackGroundDark + ");\n          --header-color-background: rgb(" + _this.settings.colorTitleCardBackGroundDark + ");\n          --cardview-border-color: rgb(" + _this.settings.colorCardBorderDark + ");\n          --cardview-embedded-title-border-right-color-edit: rgb(" + _this.settings.colorTitleCardEdgeDark + ");\n          --cardview-embedded-title-border-right-color-preview: rgb(" + _this.settings.colorTitleCardEdgeDark + ");\n        }\n        ";
            }
        };
        return _this;
    }
    CardViewModePlugin.prototype.onload = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.loadSettings()];
                    case 1:
                        _a.sent();
                        if (!this.settings.disabled)
                            this.enable();
                        this.addSettingTab(new CardViewModeSettingTab(this.app, this));
                        new CardViewModeCommands(this).addCommands();
                        return [2 /*return*/];
                }
            });
        });
    };
    CardViewModePlugin.prototype.onunload = function () {
        this.disable();
    };
    CardViewModePlugin.prototype.loadSettings = function () {
        return __awaiter(this, void 0, void 0, function () {
            var _a, _b, _c, _d;
            return __generator(this, function (_e) {
                switch (_e.label) {
                    case 0:
                        _a = this;
                        _c = (_b = Object).assign;
                        _d = [DEFAULT_SETTINGS];
                        return [4 /*yield*/, this.loadData()];
                    case 1:
                        _a.settings = _c.apply(_b, _d.concat([_e.sent()]));
                        return [2 /*return*/];
                }
            });
        });
    };
    return CardViewModePlugin;
}(obsidian.Plugin));

module.exports = CardViewModePlugin;
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibWFpbi5qcyIsInNvdXJjZXMiOlsibm9kZV9tb2R1bGVzL3RzbGliL3RzbGliLmVzNi5qcyIsInNyYy9zZXR0aW5ncy50cyIsInNyYy9tYWluLnRzIl0sInNvdXJjZXNDb250ZW50IjpbIi8qISAqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKlxyXG5Db3B5cmlnaHQgKGMpIE1pY3Jvc29mdCBDb3Jwb3JhdGlvbi5cclxuXHJcblBlcm1pc3Npb24gdG8gdXNlLCBjb3B5LCBtb2RpZnksIGFuZC9vciBkaXN0cmlidXRlIHRoaXMgc29mdHdhcmUgZm9yIGFueVxyXG5wdXJwb3NlIHdpdGggb3Igd2l0aG91dCBmZWUgaXMgaGVyZWJ5IGdyYW50ZWQuXHJcblxyXG5USEUgU09GVFdBUkUgSVMgUFJPVklERUQgXCJBUyBJU1wiIEFORCBUSEUgQVVUSE9SIERJU0NMQUlNUyBBTEwgV0FSUkFOVElFUyBXSVRIXHJcblJFR0FSRCBUTyBUSElTIFNPRlRXQVJFIElOQ0xVRElORyBBTEwgSU1QTElFRCBXQVJSQU5USUVTIE9GIE1FUkNIQU5UQUJJTElUWVxyXG5BTkQgRklUTkVTUy4gSU4gTk8gRVZFTlQgU0hBTEwgVEhFIEFVVEhPUiBCRSBMSUFCTEUgRk9SIEFOWSBTUEVDSUFMLCBESVJFQ1QsXHJcbklORElSRUNULCBPUiBDT05TRVFVRU5USUFMIERBTUFHRVMgT1IgQU5ZIERBTUFHRVMgV0hBVFNPRVZFUiBSRVNVTFRJTkcgRlJPTVxyXG5MT1NTIE9GIFVTRSwgREFUQSBPUiBQUk9GSVRTLCBXSEVUSEVSIElOIEFOIEFDVElPTiBPRiBDT05UUkFDVCwgTkVHTElHRU5DRSBPUlxyXG5PVEhFUiBUT1JUSU9VUyBBQ1RJT04sIEFSSVNJTkcgT1VUIE9GIE9SIElOIENPTk5FQ1RJT04gV0lUSCBUSEUgVVNFIE9SXHJcblBFUkZPUk1BTkNFIE9GIFRISVMgU09GVFdBUkUuXHJcbioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqICovXHJcbi8qIGdsb2JhbCBSZWZsZWN0LCBQcm9taXNlICovXHJcblxyXG52YXIgZXh0ZW5kU3RhdGljcyA9IGZ1bmN0aW9uKGQsIGIpIHtcclxuICAgIGV4dGVuZFN0YXRpY3MgPSBPYmplY3Quc2V0UHJvdG90eXBlT2YgfHxcclxuICAgICAgICAoeyBfX3Byb3RvX186IFtdIH0gaW5zdGFuY2VvZiBBcnJheSAmJiBmdW5jdGlvbiAoZCwgYikgeyBkLl9fcHJvdG9fXyA9IGI7IH0pIHx8XHJcbiAgICAgICAgZnVuY3Rpb24gKGQsIGIpIHsgZm9yICh2YXIgcCBpbiBiKSBpZiAoT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKGIsIHApKSBkW3BdID0gYltwXTsgfTtcclxuICAgIHJldHVybiBleHRlbmRTdGF0aWNzKGQsIGIpO1xyXG59O1xyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fZXh0ZW5kcyhkLCBiKSB7XHJcbiAgICBpZiAodHlwZW9mIGIgIT09IFwiZnVuY3Rpb25cIiAmJiBiICE9PSBudWxsKVxyXG4gICAgICAgIHRocm93IG5ldyBUeXBlRXJyb3IoXCJDbGFzcyBleHRlbmRzIHZhbHVlIFwiICsgU3RyaW5nKGIpICsgXCIgaXMgbm90IGEgY29uc3RydWN0b3Igb3IgbnVsbFwiKTtcclxuICAgIGV4dGVuZFN0YXRpY3MoZCwgYik7XHJcbiAgICBmdW5jdGlvbiBfXygpIHsgdGhpcy5jb25zdHJ1Y3RvciA9IGQ7IH1cclxuICAgIGQucHJvdG90eXBlID0gYiA9PT0gbnVsbCA/IE9iamVjdC5jcmVhdGUoYikgOiAoX18ucHJvdG90eXBlID0gYi5wcm90b3R5cGUsIG5ldyBfXygpKTtcclxufVxyXG5cclxuZXhwb3J0IHZhciBfX2Fzc2lnbiA9IGZ1bmN0aW9uKCkge1xyXG4gICAgX19hc3NpZ24gPSBPYmplY3QuYXNzaWduIHx8IGZ1bmN0aW9uIF9fYXNzaWduKHQpIHtcclxuICAgICAgICBmb3IgKHZhciBzLCBpID0gMSwgbiA9IGFyZ3VtZW50cy5sZW5ndGg7IGkgPCBuOyBpKyspIHtcclxuICAgICAgICAgICAgcyA9IGFyZ3VtZW50c1tpXTtcclxuICAgICAgICAgICAgZm9yICh2YXIgcCBpbiBzKSBpZiAoT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKHMsIHApKSB0W3BdID0gc1twXTtcclxuICAgICAgICB9XHJcbiAgICAgICAgcmV0dXJuIHQ7XHJcbiAgICB9XHJcbiAgICByZXR1cm4gX19hc3NpZ24uYXBwbHkodGhpcywgYXJndW1lbnRzKTtcclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fcmVzdChzLCBlKSB7XHJcbiAgICB2YXIgdCA9IHt9O1xyXG4gICAgZm9yICh2YXIgcCBpbiBzKSBpZiAoT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKHMsIHApICYmIGUuaW5kZXhPZihwKSA8IDApXHJcbiAgICAgICAgdFtwXSA9IHNbcF07XHJcbiAgICBpZiAocyAhPSBudWxsICYmIHR5cGVvZiBPYmplY3QuZ2V0T3duUHJvcGVydHlTeW1ib2xzID09PSBcImZ1bmN0aW9uXCIpXHJcbiAgICAgICAgZm9yICh2YXIgaSA9IDAsIHAgPSBPYmplY3QuZ2V0T3duUHJvcGVydHlTeW1ib2xzKHMpOyBpIDwgcC5sZW5ndGg7IGkrKykge1xyXG4gICAgICAgICAgICBpZiAoZS5pbmRleE9mKHBbaV0pIDwgMCAmJiBPYmplY3QucHJvdG90eXBlLnByb3BlcnR5SXNFbnVtZXJhYmxlLmNhbGwocywgcFtpXSkpXHJcbiAgICAgICAgICAgICAgICB0W3BbaV1dID0gc1twW2ldXTtcclxuICAgICAgICB9XHJcbiAgICByZXR1cm4gdDtcclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fZGVjb3JhdGUoZGVjb3JhdG9ycywgdGFyZ2V0LCBrZXksIGRlc2MpIHtcclxuICAgIHZhciBjID0gYXJndW1lbnRzLmxlbmd0aCwgciA9IGMgPCAzID8gdGFyZ2V0IDogZGVzYyA9PT0gbnVsbCA/IGRlc2MgPSBPYmplY3QuZ2V0T3duUHJvcGVydHlEZXNjcmlwdG9yKHRhcmdldCwga2V5KSA6IGRlc2MsIGQ7XHJcbiAgICBpZiAodHlwZW9mIFJlZmxlY3QgPT09IFwib2JqZWN0XCIgJiYgdHlwZW9mIFJlZmxlY3QuZGVjb3JhdGUgPT09IFwiZnVuY3Rpb25cIikgciA9IFJlZmxlY3QuZGVjb3JhdGUoZGVjb3JhdG9ycywgdGFyZ2V0LCBrZXksIGRlc2MpO1xyXG4gICAgZWxzZSBmb3IgKHZhciBpID0gZGVjb3JhdG9ycy5sZW5ndGggLSAxOyBpID49IDA7IGktLSkgaWYgKGQgPSBkZWNvcmF0b3JzW2ldKSByID0gKGMgPCAzID8gZChyKSA6IGMgPiAzID8gZCh0YXJnZXQsIGtleSwgcikgOiBkKHRhcmdldCwga2V5KSkgfHwgcjtcclxuICAgIHJldHVybiBjID4gMyAmJiByICYmIE9iamVjdC5kZWZpbmVQcm9wZXJ0eSh0YXJnZXQsIGtleSwgciksIHI7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX3BhcmFtKHBhcmFtSW5kZXgsIGRlY29yYXRvcikge1xyXG4gICAgcmV0dXJuIGZ1bmN0aW9uICh0YXJnZXQsIGtleSkgeyBkZWNvcmF0b3IodGFyZ2V0LCBrZXksIHBhcmFtSW5kZXgpOyB9XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX21ldGFkYXRhKG1ldGFkYXRhS2V5LCBtZXRhZGF0YVZhbHVlKSB7XHJcbiAgICBpZiAodHlwZW9mIFJlZmxlY3QgPT09IFwib2JqZWN0XCIgJiYgdHlwZW9mIFJlZmxlY3QubWV0YWRhdGEgPT09IFwiZnVuY3Rpb25cIikgcmV0dXJuIFJlZmxlY3QubWV0YWRhdGEobWV0YWRhdGFLZXksIG1ldGFkYXRhVmFsdWUpO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19hd2FpdGVyKHRoaXNBcmcsIF9hcmd1bWVudHMsIFAsIGdlbmVyYXRvcikge1xyXG4gICAgZnVuY3Rpb24gYWRvcHQodmFsdWUpIHsgcmV0dXJuIHZhbHVlIGluc3RhbmNlb2YgUCA/IHZhbHVlIDogbmV3IFAoZnVuY3Rpb24gKHJlc29sdmUpIHsgcmVzb2x2ZSh2YWx1ZSk7IH0pOyB9XHJcbiAgICByZXR1cm4gbmV3IChQIHx8IChQID0gUHJvbWlzZSkpKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcclxuICAgICAgICBmdW5jdGlvbiBmdWxmaWxsZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3IubmV4dCh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XHJcbiAgICAgICAgZnVuY3Rpb24gcmVqZWN0ZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3JbXCJ0aHJvd1wiXSh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XHJcbiAgICAgICAgZnVuY3Rpb24gc3RlcChyZXN1bHQpIHsgcmVzdWx0LmRvbmUgPyByZXNvbHZlKHJlc3VsdC52YWx1ZSkgOiBhZG9wdChyZXN1bHQudmFsdWUpLnRoZW4oZnVsZmlsbGVkLCByZWplY3RlZCk7IH1cclxuICAgICAgICBzdGVwKChnZW5lcmF0b3IgPSBnZW5lcmF0b3IuYXBwbHkodGhpc0FyZywgX2FyZ3VtZW50cyB8fCBbXSkpLm5leHQoKSk7XHJcbiAgICB9KTtcclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fZ2VuZXJhdG9yKHRoaXNBcmcsIGJvZHkpIHtcclxuICAgIHZhciBfID0geyBsYWJlbDogMCwgc2VudDogZnVuY3Rpb24oKSB7IGlmICh0WzBdICYgMSkgdGhyb3cgdFsxXTsgcmV0dXJuIHRbMV07IH0sIHRyeXM6IFtdLCBvcHM6IFtdIH0sIGYsIHksIHQsIGc7XHJcbiAgICByZXR1cm4gZyA9IHsgbmV4dDogdmVyYigwKSwgXCJ0aHJvd1wiOiB2ZXJiKDEpLCBcInJldHVyblwiOiB2ZXJiKDIpIH0sIHR5cGVvZiBTeW1ib2wgPT09IFwiZnVuY3Rpb25cIiAmJiAoZ1tTeW1ib2wuaXRlcmF0b3JdID0gZnVuY3Rpb24oKSB7IHJldHVybiB0aGlzOyB9KSwgZztcclxuICAgIGZ1bmN0aW9uIHZlcmIobikgeyByZXR1cm4gZnVuY3Rpb24gKHYpIHsgcmV0dXJuIHN0ZXAoW24sIHZdKTsgfTsgfVxyXG4gICAgZnVuY3Rpb24gc3RlcChvcCkge1xyXG4gICAgICAgIGlmIChmKSB0aHJvdyBuZXcgVHlwZUVycm9yKFwiR2VuZXJhdG9yIGlzIGFscmVhZHkgZXhlY3V0aW5nLlwiKTtcclxuICAgICAgICB3aGlsZSAoXykgdHJ5IHtcclxuICAgICAgICAgICAgaWYgKGYgPSAxLCB5ICYmICh0ID0gb3BbMF0gJiAyID8geVtcInJldHVyblwiXSA6IG9wWzBdID8geVtcInRocm93XCJdIHx8ICgodCA9IHlbXCJyZXR1cm5cIl0pICYmIHQuY2FsbCh5KSwgMCkgOiB5Lm5leHQpICYmICEodCA9IHQuY2FsbCh5LCBvcFsxXSkpLmRvbmUpIHJldHVybiB0O1xyXG4gICAgICAgICAgICBpZiAoeSA9IDAsIHQpIG9wID0gW29wWzBdICYgMiwgdC52YWx1ZV07XHJcbiAgICAgICAgICAgIHN3aXRjaCAob3BbMF0pIHtcclxuICAgICAgICAgICAgICAgIGNhc2UgMDogY2FzZSAxOiB0ID0gb3A7IGJyZWFrO1xyXG4gICAgICAgICAgICAgICAgY2FzZSA0OiBfLmxhYmVsKys7IHJldHVybiB7IHZhbHVlOiBvcFsxXSwgZG9uZTogZmFsc2UgfTtcclxuICAgICAgICAgICAgICAgIGNhc2UgNTogXy5sYWJlbCsrOyB5ID0gb3BbMV07IG9wID0gWzBdOyBjb250aW51ZTtcclxuICAgICAgICAgICAgICAgIGNhc2UgNzogb3AgPSBfLm9wcy5wb3AoKTsgXy50cnlzLnBvcCgpOyBjb250aW51ZTtcclxuICAgICAgICAgICAgICAgIGRlZmF1bHQ6XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKCEodCA9IF8udHJ5cywgdCA9IHQubGVuZ3RoID4gMCAmJiB0W3QubGVuZ3RoIC0gMV0pICYmIChvcFswXSA9PT0gNiB8fCBvcFswXSA9PT0gMikpIHsgXyA9IDA7IGNvbnRpbnVlOyB9XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKG9wWzBdID09PSAzICYmICghdCB8fCAob3BbMV0gPiB0WzBdICYmIG9wWzFdIDwgdFszXSkpKSB7IF8ubGFiZWwgPSBvcFsxXTsgYnJlYWs7IH1cclxuICAgICAgICAgICAgICAgICAgICBpZiAob3BbMF0gPT09IDYgJiYgXy5sYWJlbCA8IHRbMV0pIHsgXy5sYWJlbCA9IHRbMV07IHQgPSBvcDsgYnJlYWs7IH1cclxuICAgICAgICAgICAgICAgICAgICBpZiAodCAmJiBfLmxhYmVsIDwgdFsyXSkgeyBfLmxhYmVsID0gdFsyXTsgXy5vcHMucHVzaChvcCk7IGJyZWFrOyB9XHJcbiAgICAgICAgICAgICAgICAgICAgaWYgKHRbMl0pIF8ub3BzLnBvcCgpO1xyXG4gICAgICAgICAgICAgICAgICAgIF8udHJ5cy5wb3AoKTsgY29udGludWU7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgb3AgPSBib2R5LmNhbGwodGhpc0FyZywgXyk7XHJcbiAgICAgICAgfSBjYXRjaCAoZSkgeyBvcCA9IFs2LCBlXTsgeSA9IDA7IH0gZmluYWxseSB7IGYgPSB0ID0gMDsgfVxyXG4gICAgICAgIGlmIChvcFswXSAmIDUpIHRocm93IG9wWzFdOyByZXR1cm4geyB2YWx1ZTogb3BbMF0gPyBvcFsxXSA6IHZvaWQgMCwgZG9uZTogdHJ1ZSB9O1xyXG4gICAgfVxyXG59XHJcblxyXG5leHBvcnQgdmFyIF9fY3JlYXRlQmluZGluZyA9IE9iamVjdC5jcmVhdGUgPyAoZnVuY3Rpb24obywgbSwgaywgazIpIHtcclxuICAgIGlmIChrMiA9PT0gdW5kZWZpbmVkKSBrMiA9IGs7XHJcbiAgICBPYmplY3QuZGVmaW5lUHJvcGVydHkobywgazIsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBmdW5jdGlvbigpIHsgcmV0dXJuIG1ba107IH0gfSk7XHJcbn0pIDogKGZ1bmN0aW9uKG8sIG0sIGssIGsyKSB7XHJcbiAgICBpZiAoazIgPT09IHVuZGVmaW5lZCkgazIgPSBrO1xyXG4gICAgb1trMl0gPSBtW2tdO1xyXG59KTtcclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2V4cG9ydFN0YXIobSwgbykge1xyXG4gICAgZm9yICh2YXIgcCBpbiBtKSBpZiAocCAhPT0gXCJkZWZhdWx0XCIgJiYgIU9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbChvLCBwKSkgX19jcmVhdGVCaW5kaW5nKG8sIG0sIHApO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX192YWx1ZXMobykge1xyXG4gICAgdmFyIHMgPSB0eXBlb2YgU3ltYm9sID09PSBcImZ1bmN0aW9uXCIgJiYgU3ltYm9sLml0ZXJhdG9yLCBtID0gcyAmJiBvW3NdLCBpID0gMDtcclxuICAgIGlmIChtKSByZXR1cm4gbS5jYWxsKG8pO1xyXG4gICAgaWYgKG8gJiYgdHlwZW9mIG8ubGVuZ3RoID09PSBcIm51bWJlclwiKSByZXR1cm4ge1xyXG4gICAgICAgIG5leHQ6IGZ1bmN0aW9uICgpIHtcclxuICAgICAgICAgICAgaWYgKG8gJiYgaSA+PSBvLmxlbmd0aCkgbyA9IHZvaWQgMDtcclxuICAgICAgICAgICAgcmV0dXJuIHsgdmFsdWU6IG8gJiYgb1tpKytdLCBkb25lOiAhbyB9O1xyXG4gICAgICAgIH1cclxuICAgIH07XHJcbiAgICB0aHJvdyBuZXcgVHlwZUVycm9yKHMgPyBcIk9iamVjdCBpcyBub3QgaXRlcmFibGUuXCIgOiBcIlN5bWJvbC5pdGVyYXRvciBpcyBub3QgZGVmaW5lZC5cIik7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX3JlYWQobywgbikge1xyXG4gICAgdmFyIG0gPSB0eXBlb2YgU3ltYm9sID09PSBcImZ1bmN0aW9uXCIgJiYgb1tTeW1ib2wuaXRlcmF0b3JdO1xyXG4gICAgaWYgKCFtKSByZXR1cm4gbztcclxuICAgIHZhciBpID0gbS5jYWxsKG8pLCByLCBhciA9IFtdLCBlO1xyXG4gICAgdHJ5IHtcclxuICAgICAgICB3aGlsZSAoKG4gPT09IHZvaWQgMCB8fCBuLS0gPiAwKSAmJiAhKHIgPSBpLm5leHQoKSkuZG9uZSkgYXIucHVzaChyLnZhbHVlKTtcclxuICAgIH1cclxuICAgIGNhdGNoIChlcnJvcikgeyBlID0geyBlcnJvcjogZXJyb3IgfTsgfVxyXG4gICAgZmluYWxseSB7XHJcbiAgICAgICAgdHJ5IHtcclxuICAgICAgICAgICAgaWYgKHIgJiYgIXIuZG9uZSAmJiAobSA9IGlbXCJyZXR1cm5cIl0pKSBtLmNhbGwoaSk7XHJcbiAgICAgICAgfVxyXG4gICAgICAgIGZpbmFsbHkgeyBpZiAoZSkgdGhyb3cgZS5lcnJvcjsgfVxyXG4gICAgfVxyXG4gICAgcmV0dXJuIGFyO1xyXG59XHJcblxyXG4vKiogQGRlcHJlY2F0ZWQgKi9cclxuZXhwb3J0IGZ1bmN0aW9uIF9fc3ByZWFkKCkge1xyXG4gICAgZm9yICh2YXIgYXIgPSBbXSwgaSA9IDA7IGkgPCBhcmd1bWVudHMubGVuZ3RoOyBpKyspXHJcbiAgICAgICAgYXIgPSBhci5jb25jYXQoX19yZWFkKGFyZ3VtZW50c1tpXSkpO1xyXG4gICAgcmV0dXJuIGFyO1xyXG59XHJcblxyXG4vKiogQGRlcHJlY2F0ZWQgKi9cclxuZXhwb3J0IGZ1bmN0aW9uIF9fc3ByZWFkQXJyYXlzKCkge1xyXG4gICAgZm9yICh2YXIgcyA9IDAsIGkgPSAwLCBpbCA9IGFyZ3VtZW50cy5sZW5ndGg7IGkgPCBpbDsgaSsrKSBzICs9IGFyZ3VtZW50c1tpXS5sZW5ndGg7XHJcbiAgICBmb3IgKHZhciByID0gQXJyYXkocyksIGsgPSAwLCBpID0gMDsgaSA8IGlsOyBpKyspXHJcbiAgICAgICAgZm9yICh2YXIgYSA9IGFyZ3VtZW50c1tpXSwgaiA9IDAsIGpsID0gYS5sZW5ndGg7IGogPCBqbDsgaisrLCBrKyspXHJcbiAgICAgICAgICAgIHJba10gPSBhW2pdO1xyXG4gICAgcmV0dXJuIHI7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX3NwcmVhZEFycmF5KHRvLCBmcm9tLCBwYWNrKSB7XHJcbiAgICBpZiAocGFjayB8fCBhcmd1bWVudHMubGVuZ3RoID09PSAyKSBmb3IgKHZhciBpID0gMCwgbCA9IGZyb20ubGVuZ3RoLCBhcjsgaSA8IGw7IGkrKykge1xyXG4gICAgICAgIGlmIChhciB8fCAhKGkgaW4gZnJvbSkpIHtcclxuICAgICAgICAgICAgaWYgKCFhcikgYXIgPSBBcnJheS5wcm90b3R5cGUuc2xpY2UuY2FsbChmcm9tLCAwLCBpKTtcclxuICAgICAgICAgICAgYXJbaV0gPSBmcm9tW2ldO1xyXG4gICAgICAgIH1cclxuICAgIH1cclxuICAgIHJldHVybiB0by5jb25jYXQoYXIgfHwgZnJvbSk7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2F3YWl0KHYpIHtcclxuICAgIHJldHVybiB0aGlzIGluc3RhbmNlb2YgX19hd2FpdCA/ICh0aGlzLnYgPSB2LCB0aGlzKSA6IG5ldyBfX2F3YWl0KHYpO1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19hc3luY0dlbmVyYXRvcih0aGlzQXJnLCBfYXJndW1lbnRzLCBnZW5lcmF0b3IpIHtcclxuICAgIGlmICghU3ltYm9sLmFzeW5jSXRlcmF0b3IpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJTeW1ib2wuYXN5bmNJdGVyYXRvciBpcyBub3QgZGVmaW5lZC5cIik7XHJcbiAgICB2YXIgZyA9IGdlbmVyYXRvci5hcHBseSh0aGlzQXJnLCBfYXJndW1lbnRzIHx8IFtdKSwgaSwgcSA9IFtdO1xyXG4gICAgcmV0dXJuIGkgPSB7fSwgdmVyYihcIm5leHRcIiksIHZlcmIoXCJ0aHJvd1wiKSwgdmVyYihcInJldHVyblwiKSwgaVtTeW1ib2wuYXN5bmNJdGVyYXRvcl0gPSBmdW5jdGlvbiAoKSB7IHJldHVybiB0aGlzOyB9LCBpO1xyXG4gICAgZnVuY3Rpb24gdmVyYihuKSB7IGlmIChnW25dKSBpW25dID0gZnVuY3Rpb24gKHYpIHsgcmV0dXJuIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChhLCBiKSB7IHEucHVzaChbbiwgdiwgYSwgYl0pID4gMSB8fCByZXN1bWUobiwgdik7IH0pOyB9OyB9XHJcbiAgICBmdW5jdGlvbiByZXN1bWUobiwgdikgeyB0cnkgeyBzdGVwKGdbbl0odikpOyB9IGNhdGNoIChlKSB7IHNldHRsZShxWzBdWzNdLCBlKTsgfSB9XHJcbiAgICBmdW5jdGlvbiBzdGVwKHIpIHsgci52YWx1ZSBpbnN0YW5jZW9mIF9fYXdhaXQgPyBQcm9taXNlLnJlc29sdmUoci52YWx1ZS52KS50aGVuKGZ1bGZpbGwsIHJlamVjdCkgOiBzZXR0bGUocVswXVsyXSwgcik7IH1cclxuICAgIGZ1bmN0aW9uIGZ1bGZpbGwodmFsdWUpIHsgcmVzdW1lKFwibmV4dFwiLCB2YWx1ZSk7IH1cclxuICAgIGZ1bmN0aW9uIHJlamVjdCh2YWx1ZSkgeyByZXN1bWUoXCJ0aHJvd1wiLCB2YWx1ZSk7IH1cclxuICAgIGZ1bmN0aW9uIHNldHRsZShmLCB2KSB7IGlmIChmKHYpLCBxLnNoaWZ0KCksIHEubGVuZ3RoKSByZXN1bWUocVswXVswXSwgcVswXVsxXSk7IH1cclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fYXN5bmNEZWxlZ2F0b3Iobykge1xyXG4gICAgdmFyIGksIHA7XHJcbiAgICByZXR1cm4gaSA9IHt9LCB2ZXJiKFwibmV4dFwiKSwgdmVyYihcInRocm93XCIsIGZ1bmN0aW9uIChlKSB7IHRocm93IGU7IH0pLCB2ZXJiKFwicmV0dXJuXCIpLCBpW1N5bWJvbC5pdGVyYXRvcl0gPSBmdW5jdGlvbiAoKSB7IHJldHVybiB0aGlzOyB9LCBpO1xyXG4gICAgZnVuY3Rpb24gdmVyYihuLCBmKSB7IGlbbl0gPSBvW25dID8gZnVuY3Rpb24gKHYpIHsgcmV0dXJuIChwID0gIXApID8geyB2YWx1ZTogX19hd2FpdChvW25dKHYpKSwgZG9uZTogbiA9PT0gXCJyZXR1cm5cIiB9IDogZiA/IGYodikgOiB2OyB9IDogZjsgfVxyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19hc3luY1ZhbHVlcyhvKSB7XHJcbiAgICBpZiAoIVN5bWJvbC5hc3luY0l0ZXJhdG9yKSB0aHJvdyBuZXcgVHlwZUVycm9yKFwiU3ltYm9sLmFzeW5jSXRlcmF0b3IgaXMgbm90IGRlZmluZWQuXCIpO1xyXG4gICAgdmFyIG0gPSBvW1N5bWJvbC5hc3luY0l0ZXJhdG9yXSwgaTtcclxuICAgIHJldHVybiBtID8gbS5jYWxsKG8pIDogKG8gPSB0eXBlb2YgX192YWx1ZXMgPT09IFwiZnVuY3Rpb25cIiA/IF9fdmFsdWVzKG8pIDogb1tTeW1ib2wuaXRlcmF0b3JdKCksIGkgPSB7fSwgdmVyYihcIm5leHRcIiksIHZlcmIoXCJ0aHJvd1wiKSwgdmVyYihcInJldHVyblwiKSwgaVtTeW1ib2wuYXN5bmNJdGVyYXRvcl0gPSBmdW5jdGlvbiAoKSB7IHJldHVybiB0aGlzOyB9LCBpKTtcclxuICAgIGZ1bmN0aW9uIHZlcmIobikgeyBpW25dID0gb1tuXSAmJiBmdW5jdGlvbiAodikgeyByZXR1cm4gbmV3IFByb21pc2UoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkgeyB2ID0gb1tuXSh2KSwgc2V0dGxlKHJlc29sdmUsIHJlamVjdCwgdi5kb25lLCB2LnZhbHVlKTsgfSk7IH07IH1cclxuICAgIGZ1bmN0aW9uIHNldHRsZShyZXNvbHZlLCByZWplY3QsIGQsIHYpIHsgUHJvbWlzZS5yZXNvbHZlKHYpLnRoZW4oZnVuY3Rpb24odikgeyByZXNvbHZlKHsgdmFsdWU6IHYsIGRvbmU6IGQgfSk7IH0sIHJlamVjdCk7IH1cclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fbWFrZVRlbXBsYXRlT2JqZWN0KGNvb2tlZCwgcmF3KSB7XHJcbiAgICBpZiAoT2JqZWN0LmRlZmluZVByb3BlcnR5KSB7IE9iamVjdC5kZWZpbmVQcm9wZXJ0eShjb29rZWQsIFwicmF3XCIsIHsgdmFsdWU6IHJhdyB9KTsgfSBlbHNlIHsgY29va2VkLnJhdyA9IHJhdzsgfVxyXG4gICAgcmV0dXJuIGNvb2tlZDtcclxufTtcclxuXHJcbnZhciBfX3NldE1vZHVsZURlZmF1bHQgPSBPYmplY3QuY3JlYXRlID8gKGZ1bmN0aW9uKG8sIHYpIHtcclxuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShvLCBcImRlZmF1bHRcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdiB9KTtcclxufSkgOiBmdW5jdGlvbihvLCB2KSB7XHJcbiAgICBvW1wiZGVmYXVsdFwiXSA9IHY7XHJcbn07XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19pbXBvcnRTdGFyKG1vZCkge1xyXG4gICAgaWYgKG1vZCAmJiBtb2QuX19lc01vZHVsZSkgcmV0dXJuIG1vZDtcclxuICAgIHZhciByZXN1bHQgPSB7fTtcclxuICAgIGlmIChtb2QgIT0gbnVsbCkgZm9yICh2YXIgayBpbiBtb2QpIGlmIChrICE9PSBcImRlZmF1bHRcIiAmJiBPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwobW9kLCBrKSkgX19jcmVhdGVCaW5kaW5nKHJlc3VsdCwgbW9kLCBrKTtcclxuICAgIF9fc2V0TW9kdWxlRGVmYXVsdChyZXN1bHQsIG1vZCk7XHJcbiAgICByZXR1cm4gcmVzdWx0O1xyXG59XHJcblxyXG5leHBvcnQgZnVuY3Rpb24gX19pbXBvcnREZWZhdWx0KG1vZCkge1xyXG4gICAgcmV0dXJuIChtb2QgJiYgbW9kLl9fZXNNb2R1bGUpID8gbW9kIDogeyBkZWZhdWx0OiBtb2QgfTtcclxufVxyXG5cclxuZXhwb3J0IGZ1bmN0aW9uIF9fY2xhc3NQcml2YXRlRmllbGRHZXQocmVjZWl2ZXIsIHN0YXRlLCBraW5kLCBmKSB7XHJcbiAgICBpZiAoa2luZCA9PT0gXCJhXCIgJiYgIWYpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJQcml2YXRlIGFjY2Vzc29yIHdhcyBkZWZpbmVkIHdpdGhvdXQgYSBnZXR0ZXJcIik7XHJcbiAgICBpZiAodHlwZW9mIHN0YXRlID09PSBcImZ1bmN0aW9uXCIgPyByZWNlaXZlciAhPT0gc3RhdGUgfHwgIWYgOiAhc3RhdGUuaGFzKHJlY2VpdmVyKSkgdGhyb3cgbmV3IFR5cGVFcnJvcihcIkNhbm5vdCByZWFkIHByaXZhdGUgbWVtYmVyIGZyb20gYW4gb2JqZWN0IHdob3NlIGNsYXNzIGRpZCBub3QgZGVjbGFyZSBpdFwiKTtcclxuICAgIHJldHVybiBraW5kID09PSBcIm1cIiA/IGYgOiBraW5kID09PSBcImFcIiA/IGYuY2FsbChyZWNlaXZlcikgOiBmID8gZi52YWx1ZSA6IHN0YXRlLmdldChyZWNlaXZlcik7XHJcbn1cclxuXHJcbmV4cG9ydCBmdW5jdGlvbiBfX2NsYXNzUHJpdmF0ZUZpZWxkU2V0KHJlY2VpdmVyLCBzdGF0ZSwgdmFsdWUsIGtpbmQsIGYpIHtcclxuICAgIGlmIChraW5kID09PSBcIm1cIikgdGhyb3cgbmV3IFR5cGVFcnJvcihcIlByaXZhdGUgbWV0aG9kIGlzIG5vdCB3cml0YWJsZVwiKTtcclxuICAgIGlmIChraW5kID09PSBcImFcIiAmJiAhZikgdGhyb3cgbmV3IFR5cGVFcnJvcihcIlByaXZhdGUgYWNjZXNzb3Igd2FzIGRlZmluZWQgd2l0aG91dCBhIHNldHRlclwiKTtcclxuICAgIGlmICh0eXBlb2Ygc3RhdGUgPT09IFwiZnVuY3Rpb25cIiA/IHJlY2VpdmVyICE9PSBzdGF0ZSB8fCAhZiA6ICFzdGF0ZS5oYXMocmVjZWl2ZXIpKSB0aHJvdyBuZXcgVHlwZUVycm9yKFwiQ2Fubm90IHdyaXRlIHByaXZhdGUgbWVtYmVyIHRvIGFuIG9iamVjdCB3aG9zZSBjbGFzcyBkaWQgbm90IGRlY2xhcmUgaXRcIik7XHJcbiAgICByZXR1cm4gKGtpbmQgPT09IFwiYVwiID8gZi5jYWxsKHJlY2VpdmVyLCB2YWx1ZSkgOiBmID8gZi52YWx1ZSA9IHZhbHVlIDogc3RhdGUuc2V0KHJlY2VpdmVyLCB2YWx1ZSkpLCB2YWx1ZTtcclxufVxyXG4iLCJpbXBvcnQgeyBcbiAgQXBwLCBcbiAgQnV0dG9uQ29tcG9uZW50LCBcbiAgUGx1Z2luLCBcbiAgUGx1Z2luU2V0dGluZ1RhYiwgXG4gIFNldHRpbmcsIFxufSBmcm9tICdvYnNpZGlhbic7XG5cblxuZGVjbGFyZSBjbGFzcyBDYXJkVmlld01vZGVQbHVnaW4gZXh0ZW5kcyBQbHVnaW4ge1xuICBzZXR0aW5nczogQ2FyZFZpZXdNb2RlU2V0dGluZ3M7XG4gIGRpc2FibGUoKTogdm9pZDtcbiAgZW5hYmxlKCk6IHZvaWQ7XG4gIHJlZnJlc2goKTogdm9pZDtcbn1cblxuXG5leHBvcnQgaW50ZXJmYWNlIENhcmRWaWV3TW9kZVNldHRpbmdzIHtcbiAgZGlzYWJsZWQ6IGJvb2xlYW47XG4gIGNhcmRUaXRsZTogYm9vbGVhbjtcbiAgY2FyZENvcm5lclJhZGl1czogbnVtYmVyO1xuICBjYXJkVGl0bGVDb3JuZXJSYWRpdXM6IG51bWJlcjtcbiAgY2FyZERyb3BTaGFkb3c6IGJvb2xlYW47XG4gIHJlbW92ZVBhbmVCb3VuZGFyaWVzOiBib29sZWFuO1xuICBoaWRlU2Nyb2xsQmFyOiBib29sZWFuO1xuICBjb2xvckJhY2tHcm91bmRMaWdodDogc3RyaW5nO1xuICBjb2xvckJhY2tHcm91bmREYXJrOiBzdHJpbmc7XG4gIGNvbG9yQWN0aXZlQ2FyZExpZ2h0OiBzdHJpbmc7XG4gIGNvbG9yQWN0aXZlQ2FyZERhcms6IHN0cmluZztcbiAgY29sb3JOb25BY3RpdmVDYXJkRGFyazogc3RyaW5nO1xuICBjb2xvck5vbkFjdGl2ZUNhcmRMaWdodDogc3RyaW5nO1xuICBjb2xvclRpdGxlQ2FyZEVkZ2VEYXJrOiBzdHJpbmc7XG4gIGNvbG9yVGl0bGVDYXJkRWRnZUxpZ2h0OiBzdHJpbmc7XG4gIGNvbG9yVGl0bGVDYXJkQmFja0dyb3VuZExpZ2h0OiBzdHJpbmc7XG4gIGNvbG9yVGl0bGVDYXJkQmFja0dyb3VuZERhcms6IHN0cmluZztcbiAgY29sb3JEaWZmQmV0d2VlbkFjdGl2ZTogbnVtYmVyO1xuICBjb2xvckNhcmRCb3JkZXJMaWdodDogc3RyaW5nO1xuICBjb2xvckNhcmRCb3JkZXJEYXJrOiBzdHJpbmc7XG59XG5cbmV4cG9ydCBjb25zdCBERUZBVUxUX1NFVFRJTkdTOiBDYXJkVmlld01vZGVTZXR0aW5ncyA9IHtcbiAgZGlzYWJsZWQ6IHRydWUsXG4gIGNhcmRUaXRsZTogZmFsc2UsXG4gIGNhcmRDb3JuZXJSYWRpdXM6IDEwLFxuICBjYXJkVGl0bGVDb3JuZXJSYWRpdXM6IDUsXG4gIGNhcmREcm9wU2hhZG93OiB0cnVlLFxuICByZW1vdmVQYW5lQm91bmRhcmllczogdHJ1ZSxcbiAgaGlkZVNjcm9sbEJhcjogdHJ1ZSxcbiAgY29sb3JCYWNrR3JvdW5kTGlnaHQ6IFwiMjU1LCAyNTUsIDI1NVwiLFxuICBjb2xvckJhY2tHcm91bmREYXJrOiBcIjE0NSwgMTQ1LCAxNDVcIixcbiAgY29sb3JBY3RpdmVDYXJkTGlnaHQ6IFwiMjU1LCAyNTUsIDI1NVwiLFxuICBjb2xvckFjdGl2ZUNhcmREYXJrOiBcIjcxLCA3MSwgNzFcIixcbiAgY29sb3JOb25BY3RpdmVDYXJkRGFyazogXCI3MSwgNzEsIDcxXCIsXG4gIGNvbG9yTm9uQWN0aXZlQ2FyZExpZ2h0OiBcIjI1NSwgMjU1LCAyNTVcIixcbiAgY29sb3JUaXRsZUNhcmRFZGdlRGFyazogXCIyMjcsIDc2LCAzOFwiLFxuICBjb2xvclRpdGxlQ2FyZEVkZ2VMaWdodDogXCIxMjMsIDIzMCwgMVwiLFxuICBjb2xvclRpdGxlQ2FyZEJhY2tHcm91bmRMaWdodDogXCIyNDIsIDI0MiwgMjQyXCIsXG4gIGNvbG9yVGl0bGVDYXJkQmFja0dyb3VuZERhcms6IFwiMCwgMCwgMFwiLFxuICBjb2xvckRpZmZCZXR3ZWVuQWN0aXZlOiAwLFxuICBjb2xvckNhcmRCb3JkZXJMaWdodDogXCIyNTUsIDI1NSwgMjU1XCIsXG4gIGNvbG9yQ2FyZEJvcmRlckRhcms6IFwiMCwgMCwgMFwiXG59XG5cbmV4cG9ydCBjbGFzcyBDYXJkVmlld01vZGVTZXR0aW5nVGFiIGV4dGVuZHMgUGx1Z2luU2V0dGluZ1RhYiB7XG4gIHBsdWdpbjogQ2FyZFZpZXdNb2RlUGx1Z2luO1xuICBjb25zdHJ1Y3RvcihhcHA6IEFwcCwgcGx1Z2luOiBDYXJkVmlld01vZGVQbHVnaW4pIHtcbiAgICBzdXBlcihhcHAsIHBsdWdpbik7XG4gICAgdGhpcy5wbHVnaW4gPSBwbHVnaW47XG4gIH1cblxuICBkaXNwbGF5KCk6IHZvaWQge1xuICAgIHRoaXMuY29udGFpbmVyRWwuZW1wdHkoKTtcblxuXG4gICAgdGhpcy5jb250YWluZXJFbC5jcmVhdGVFbChcImgzXCIsIHtcbiAgICAgIHRleHQ6IFwiR2VuZXJhbCBTZXR0aW5nc1wiLFxuICAgIH0pO1xuICAgIHRoaXMuYWRkU2V0dGluZ1RvZ2dsZUNhcmRWaWV3KCk7XG4gICAgdGhpcy5hZGRTZXR0aW5nVG9nZ2xlQ2FyZFRpdGxlKCk7XG4gICAgXG4gICAgdGhpcy5jb250YWluZXJFbC5jcmVhdGVFbChcImgzXCIsIHtcbiAgICAgIHRleHQ6IFwiQ2FyZCBWaWV3IERlc2lnbmVyXCIsXG4gICAgfSk7XG4gICAgdGhpcy5hZGRTZXR0aW5nVG9nZ2xlUmVtb3ZlUGFuZUJvdW5kYXJpaWVzKCk7XG4gICAgdGhpcy5hZGRTZXR0aW5nSGlkZVNjcm9sbGJhcigpO1xuICAgIHRoaXMuYWRkU2V0dGluZ1RvZ2dsZURyb3BTaGFkb3coKTtcbiAgICB0aGlzLmFkZFNldHRpbmdDYXJkQ29ybmVyUmFkaXVzKCk7XG4gICAgdGhpcy5hZGRTZXR0aW5nQ2FyZFRpdGxlQ29ybmVyUmFkaXVzKCk7XG4gICAgXG4gICAgdGhpcy5jb250YWluZXJFbC5jcmVhdGVFbChcImg0XCIsIHtcbiAgICAgIHRleHQ6IFwiRGFyayBNb2RlIENvbG9yXCIsXG4gICAgfSk7XG4gICAgdGhpcy5hZGRTZXR0aW5nQmFja2dyb3VuZENvbG9yRGFyaygpO1xuICAgIHRoaXMuYWRkU2V0dGluZ0FjdGl2ZUNhcmRDb2xvckRhcmsoKTtcbiAgICB0aGlzLmFkZFNldHRpbmdDYXJkQm9yZGVyQ29sb3JEYXJrKCk7XG4gICAgdGhpcy5hZGRTZXR0aW5nQ2FyZFRpdGxlQmFja2dyb3VuZENvbG9yRGFyaygpO1xuICAgIHRoaXMuYWRkU2V0dGluZ0NhcmRUaXRsZUVkZ2VDb2xvckRhcmsoKTtcblxuICAgIC8vIGRldGFpbCBzdW1tYXJ5XG4gICAgLy8gdGhpcy5jb250YWluZXJFbC5jcmVhdGVFbChcImRldGFpbHNcIiwge1xuICAgIC8vICAgdGV4dDogXCJcIlxuICAgIC8vIH0pLmNyZWF0ZUVsKFwic3VtbWFyeVwiLCB7XG4gICAgLy8gICB0ZXh0OiBcIkRhcmsgbW9kZVwiLFxuICAgIC8vIH0pO1xuICAgIFxuICAgIFxuXG4gICAgdGhpcy5jb250YWluZXJFbC5jcmVhdGVFbChcImg0XCIsIHtcbiAgICAgIHRleHQ6IFwiTGlnaHQgTW9kZSBDb2xvclwiXG4gICAgfSk7XG4gICAgdGhpcy5hZGRTZXR0aW5nQmFja2dyb3VuZENvbG9yTGlnaHQoKTtcbiAgICB0aGlzLmFkZFNldHRpbmdBY3RpdmVDYXJkQ29sb3JMaWdodCgpO1xuICAgIHRoaXMuYWRkU2V0dGluZ0NhcmRCb3JkZXJDb2xvckxpZ2h0KCk7XG4gICAgdGhpcy5hZGRTZXR0aW5nQ2FyZFRpdGxlQmFja2dyb3VuZENvbG9yTGlnaHQoKTtcbiAgICB0aGlzLmFkZFNldHRpbmdDYXJkVGl0bGVFZGdlQ29sb3JMaWdodCgpO1xuXG5cbiAgICB0aGlzLmNvbnRhaW5lckVsLmNyZWF0ZUVsKFwiaDNcIiwge1xuICAgICAgdGV4dDogXCJBdHRlbnRpb24gUGFuZVwiLFxuICAgIH0pO1xuICAgIHRoaXMuYWRkU2V0dGluZ0RpZmZCZXRBY3RpdmVOb25hY3RpdmUoKTtcbiAgICBcbiAgfVxuICBcbiAgYWRkU2V0dGluZ1RvZ2dsZUNhcmRWaWV3KCk6IHZvaWQge1xuICAgIG5ldyBTZXR0aW5nKHRoaXMuY29udGFpbmVyRWwpXG4gICAgLnNldE5hbWUoXCJUb2dnbGUgQ2FyZCBWaWV3XCIpXG4gICAgLnNldERlc2MoXCJUdXJucyBjYXJkIHZpZXcgbW9kZSBvbiBvciBvZmYgZ2xvYmFsbHkuXCIpXG4gICAgLmFkZFRvZ2dsZSh0b2dnbGUgPT4gdG9nZ2xlLnNldFZhbHVlKCF0aGlzLnBsdWdpbi5zZXR0aW5ncy5kaXNhYmxlZClcbiAgICAub25DaGFuZ2UoKHZhbHVlKSA9PiB7XG4gICAgICB0aGlzLnBsdWdpbi5zZXR0aW5ncy5kaXNhYmxlZCA9ICF2YWx1ZTtcbiAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKTtcbiAgICAgIGlmICh0aGlzLnBsdWdpbi5zZXR0aW5ncy5kaXNhYmxlZCkge1xuICAgICAgICB0aGlzLnBsdWdpbi5kaXNhYmxlKCk7XG4gICAgICB9XG4gICAgICBlbHNlIHtcbiAgICAgICAgdGhpcy5wbHVnaW4uZW5hYmxlKCk7XG4gICAgICB9XG4gICAgfSlcbiAgICApO1xuICB9XG4gIFxuICBhZGRTZXR0aW5nVG9nZ2xlQ2FyZFRpdGxlKCk6IHZvaWQge1xuICAgIG5ldyBTZXR0aW5nKHRoaXMuY29udGFpbmVyRWwpXG4gICAgICAuc2V0TmFtZShcIlRvZ2dsZSBUaXRsZSBDYXJkXCIpXG4gICAgICAuc2V0RGVzYyhcIlZpZXcgbm90ZSB0aXRsZXMgYXMgY2FyZHMuIEVuYWJsZSB0aGlzIG9wdGlvbiB3aXRoIEVtYmVkZGVkIE5vdGUgVGl0bGUgUGx1Z2luLlwiKVxuICAgICAgLmFkZFRvZ2dsZSh0b2dnbGUgPT4gdG9nZ2xlLnNldFZhbHVlKHRoaXMucGx1Z2luLnNldHRpbmdzLmNhcmRUaXRsZSlcbiAgICAgICAgLm9uQ2hhbmdlKCh2YWx1ZSkgPT4ge1xuICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNhcmRUaXRsZSA9IHZhbHVlO1xuICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKTtcbiAgICAgICAgICB0aGlzLnBsdWdpbi5yZWZyZXNoKCk7XG4gICAgICAgIH1cbiAgICAgIClcbiAgICApO1xuICB9XG5cbiAgYWRkU2V0dGluZ1RvZ2dsZVJlbW92ZVBhbmVCb3VuZGFyaWllcygpOiB2b2lkIHtcbiAgICBuZXcgU2V0dGluZyh0aGlzLmNvbnRhaW5lckVsKVxuICAgICAgLnNldE5hbWUoXCJSZW1vdmUgUGFuZSBCb3VuZGFyaWVzXCIpXG4gICAgICAuc2V0RGVzYyhcIldoZW4gdG9nZ2xlZCwgcGFuZSBib3VuZGFyaWVzIHdpbGwgYmUgcmVtb3ZlZC5cIilcbiAgICAgIC5hZGRUb2dnbGUodG9nZ2xlID0+IHRvZ2dsZS5zZXRWYWx1ZSh0aGlzLnBsdWdpbi5zZXR0aW5ncy5yZW1vdmVQYW5lQm91bmRhcmllcylcbiAgICAgICAgLm9uQ2hhbmdlKCh2YWx1ZSkgPT4ge1xuICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLnJlbW92ZVBhbmVCb3VuZGFyaWVzID0gdmFsdWU7XG4gICAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpO1xuICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgfVxuICAgICAgKVxuICAgICk7ICAgICAgICBcbiAgfVxuXG4gIGFkZFNldHRpbmdIaWRlU2Nyb2xsYmFyKCk6IHZvaWQge1xuICAgIG5ldyBTZXR0aW5nKHRoaXMuY29udGFpbmVyRWwpXG4gICAgICAuc2V0TmFtZShcIkhpZGUgc2Nyb2xsYmFyXCIpXG4gICAgICAuc2V0RGVzYyhcIldoZW4gdG9nZ2xlZCwgc2Nyb2xsYmFyIHdpbGwgYmUgcmVtb3ZlZC5cIilcbiAgICAgIC5hZGRUb2dnbGUodG9nZ2xlID0+IHRvZ2dsZS5zZXRWYWx1ZSh0aGlzLnBsdWdpbi5zZXR0aW5ncy5oaWRlU2Nyb2xsQmFyKVxuICAgICAgICAub25DaGFuZ2UoKHZhbHVlKSA9PiB7XG4gICAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuaGlkZVNjcm9sbEJhciA9IHZhbHVlO1xuICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKTtcbiAgICAgICAgICB0aGlzLnBsdWdpbi5yZWZyZXNoKCk7XG4gICAgICAgIH1cbiAgICAgIClcbiAgICApOyAgICAgICAgICAgIFxuICB9XG5cbiAgYWRkU2V0dGluZ1RvZ2dsZURyb3BTaGFkb3coKTogdm9pZCB7XG4gICAgbmV3IFNldHRpbmcodGhpcy5jb250YWluZXJFbClcbiAgICAgIC5zZXROYW1lKFwiRHJvcCBTaGFkb3dcIilcbiAgICAgIC5zZXREZXNjKFwiV2hlbiB0b2dnbGVkLCBkcm9wcGluZyBzaGFkb3cgdG8gY2FyZHMgd2lsbCBiZSBhY3RpdmF0ZWQuXCIpXG4gICAgICAuYWRkVG9nZ2xlKHRvZ2dsZSA9PiB0b2dnbGUuc2V0VmFsdWUodGhpcy5wbHVnaW4uc2V0dGluZ3MuY2FyZERyb3BTaGFkb3cpXG4gICAgICAgIC5vbkNoYW5nZSgodmFsdWUpID0+IHtcbiAgICAgICAgICB0aGlzLnBsdWdpbi5zZXR0aW5ncy5jYXJkRHJvcFNoYWRvdyA9IHZhbHVlO1xuICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKTtcbiAgICAgICAgICB0aGlzLnBsdWdpbi5yZWZyZXNoKCk7XG4gICAgICAgIH1cbiAgICAgIClcbiAgICApOyAgICBcbiAgfVxuICBcbiAgYWRkU2V0dGluZ0NhcmRDb3JuZXJSYWRpdXMoKTogdm9pZCB7XG4gICAgbmV3IFNldHRpbmcodGhpcy5jb250YWluZXJFbClcbiAgICAgIC5zZXROYW1lKCdDYXJkIENvcm5lciBSYWRpdXMnKVxuICAgICAgLnNldERlc2MoJ1NldCBudW1iZXIgdG8gYWRqdXN0IGNhcmQgY29ybmVyIHJhZGl1cy4gRGVmYXVsdCByYWRpdXMgaXMgMTBweC4nKVxuICAgICAgLmFkZFRleHQodGV4dCA9PiB0ZXh0LnNldFBsYWNlaG9sZGVyKCdEZWZhdWx0OiAxMHB4JylcbiAgICAgICAgLnNldFZhbHVlKCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jYXJkQ29ybmVyUmFkaXVzIHx8ICcnKSArICcnKVxuICAgICAgICAub25DaGFuZ2UoKHZhbHVlKSA9PiB7XG4gICAgICAgICAgbGV0IG51ID0gTnVtYmVyKHZhbHVlKVxuICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNhcmRDb3JuZXJSYWRpdXMgPSBudTtcbiAgICAgICAgICB0aGlzLnBsdWdpbi5zYXZlRGF0YSh0aGlzLnBsdWdpbi5zZXR0aW5ncyk7XG4gICAgICAgICAgdGhpcy5wbHVnaW4ucmVmcmVzaCgpO1xuICAgICAgICB9KVxuICAgICk7XG4gIH1cblxuICBhZGRTZXR0aW5nQ2FyZFRpdGxlQ29ybmVyUmFkaXVzKCk6IHZvaWQge1xuICAgIG5ldyBTZXR0aW5nKHRoaXMuY29udGFpbmVyRWwpXG4gICAgICAuc2V0TmFtZSgnVGl0bGUgQ2FyZCBDb3JuZXIgUmFkaXVzJylcbiAgICAgIC5zZXREZXNjKCdTZXQgbnVtYmVyIHRvIGFkanVzdCB0aXRsZSBjYXJkIGNvcm5lciByYWRpdXMuIERlZmF1bHQgcmFkaXVzIGlzIDVweC4nKVxuICAgICAgLmFkZFRleHQodGV4dCA9PiB0ZXh0LnNldFBsYWNlaG9sZGVyKCdEZWZhdWx0OiA1cHgnKVxuICAgICAgICAuc2V0VmFsdWUoKHRoaXMucGx1Z2luLnNldHRpbmdzLmNhcmRUaXRsZUNvcm5lclJhZGl1cyB8fCAnJykgKyAnJylcbiAgICAgICAgLm9uQ2hhbmdlKCh2YWx1ZSkgPT4ge1xuICAgICAgICAgIGxldCBudSA9IE51bWJlcih2YWx1ZSlcbiAgICAgICAgICB0aGlzLnBsdWdpbi5zZXR0aW5ncy5jYXJkVGl0bGVDb3JuZXJSYWRpdXMgPSBudTtcbiAgICAgICAgICB0aGlzLnBsdWdpbi5zYXZlRGF0YSh0aGlzLnBsdWdpbi5zZXR0aW5ncyk7XG4gICAgICAgICAgdGhpcy5wbHVnaW4ucmVmcmVzaCgpO1xuICAgICAgICB9KVxuICAgICk7ICAgIFxuICB9XG5cbiAgYWRkU2V0dGluZ0NhcmRCb3JkZXJDb2xvckxpZ2h0KCk6IHZvaWQge1xuICAgIG5ldyBTZXR0aW5nKHRoaXMuY29udGFpbmVyRWwpXG4gICAgICAuc2V0TmFtZShcIkNhcmQgQm9yZGVyIENvbG9yXCIpXG4gICAgICAuY29udHJvbEVsLmNyZWF0ZUVsKFxuICAgICAgICBcImlucHV0XCIsXG4gICAgICAgIHtcbiAgICAgICAgICAgIHR5cGU6IFwiY29sb3JcIixcbiAgICAgICAgICAgIHZhbHVlOiByZ2JUb0hleCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckNhcmRCb3JkZXJMaWdodClcbiAgICAgICAgfSxcbiAgICAgICAgKGVsKSA9PiB7XG4gICAgICAgICAgICBlbC52YWx1ZSA9IHJnYlRvSGV4KHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yQ2FyZEJvcmRlckxpZ2h0KTtcbiAgICAgICAgICAgIGVsLm9uaW5wdXQgPSAoeyB0YXJnZXQgfSkgPT4ge1xuICAgICAgICAgICAgICAgIGxldCBjb2xvciA9IGhleFRvUmdiKCh0YXJnZXQgYXMgSFRNTElucHV0RWxlbWVudCkudmFsdWUpO1xuICBcbiAgICAgICAgICAgICAgICBpZiAoIWNvbG9yKSByZXR1cm47XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JDYXJkQm9yZGVyTGlnaHQgPSBgJHtjb2xvci5yfSwgJHtjb2xvci5nfSwgJHtjb2xvci5ifWA7XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpXG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4ucmVmcmVzaCgpO1xuICAgICAgICAgICAgfTtcbiAgICAgICAgfVxuICAgICk7XG4gIH1cbiAgXG4gIGFkZFNldHRpbmdDYXJkQm9yZGVyQ29sb3JEYXJrKCk6IHZvaWQge1xuICAgIG5ldyBTZXR0aW5nKHRoaXMuY29udGFpbmVyRWwpXG4gICAgICAuc2V0TmFtZShcIkNhcmQgQm9yZGVyIENvbG9yXCIpXG4gICAgICAuY29udHJvbEVsLmNyZWF0ZUVsKFxuICAgICAgICBcImlucHV0XCIsXG4gICAgICAgIHtcbiAgICAgICAgICAgIHR5cGU6IFwiY29sb3JcIixcbiAgICAgICAgICAgIHZhbHVlOiByZ2JUb0hleCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckNhcmRCb3JkZXJEYXJrKVxuICAgICAgICB9LFxuICAgICAgICAoZWwpID0+IHtcbiAgICAgICAgICAgIGVsLnZhbHVlID0gcmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JDYXJkQm9yZGVyRGFyayk7XG4gICAgICAgICAgICBlbC5vbmlucHV0ID0gKHsgdGFyZ2V0IH0pID0+IHtcbiAgICAgICAgICAgICAgICBsZXQgY29sb3IgPSBoZXhUb1JnYigodGFyZ2V0IGFzIEhUTUxJbnB1dEVsZW1lbnQpLnZhbHVlKTtcblxuICAgICAgICAgICAgICAgIGlmICghY29sb3IpIHJldHVybjtcbiAgICAgICAgICAgICAgICB0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckNhcmRCb3JkZXJEYXJrID0gYCR7Y29sb3Iucn0sICR7Y29sb3IuZ30sICR7Y29sb3IuYn1gO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKVxuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgICAgIH07XG4gICAgICAgIH1cbiAgICApO1xuICB9XG4gIFxuICBhZGRTZXR0aW5nQmFja2dyb3VuZENvbG9yTGlnaHQoKTogdm9pZCB7XG4gICAgbmV3IFNldHRpbmcodGhpcy5jb250YWluZXJFbClcbiAgICAgIC5zZXROYW1lKFwiQmFja2dyb3VuZCBDb2xvclwiKVxuICAgICAgLmNvbnRyb2xFbC5jcmVhdGVFbChcbiAgICAgICAgXCJpbnB1dFwiLFxuICAgICAgICB7XG4gICAgICAgICAgICB0eXBlOiBcImNvbG9yXCIsXG4gICAgICAgICAgICB2YWx1ZTogcmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JCYWNrR3JvdW5kTGlnaHQpXG4gICAgICAgIH0sXG4gICAgICAgIChlbCkgPT4ge1xuICAgICAgICAgICAgZWwudmFsdWUgPSByZ2JUb0hleCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckJhY2tHcm91bmRMaWdodCk7XG4gICAgICAgICAgICBlbC5vbmlucHV0ID0gKHsgdGFyZ2V0IH0pID0+IHtcbiAgICAgICAgICAgICAgICBsZXQgY29sb3IgPSBoZXhUb1JnYigodGFyZ2V0IGFzIEhUTUxJbnB1dEVsZW1lbnQpLnZhbHVlKTtcbiAgXG4gICAgICAgICAgICAgICAgaWYgKCFjb2xvcikgcmV0dXJuO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yQmFja0dyb3VuZExpZ2h0ID0gYCR7Y29sb3Iucn0sICR7Y29sb3IuZ30sICR7Y29sb3IuYn1gO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKVxuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgICAgIH07XG4gICAgICAgIH1cbiAgICApO1xuICB9XG4gIFxuICBhZGRTZXR0aW5nQmFja2dyb3VuZENvbG9yRGFyaygpOiB2b2lkIHtcbiAgICBuZXcgU2V0dGluZyh0aGlzLmNvbnRhaW5lckVsKVxuICAgICAgLnNldE5hbWUoXCJCYWNrZ3JvdW5kIENvbG9yXCIpXG4gICAgICAuY29udHJvbEVsLmNyZWF0ZUVsKFxuICAgICAgICBcImlucHV0XCIsXG4gICAgICAgIHtcbiAgICAgICAgICAgIHR5cGU6IFwiY29sb3JcIixcbiAgICAgICAgICAgIHZhbHVlOiByZ2JUb0hleCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckJhY2tHcm91bmREYXJrKVxuICAgICAgICB9LFxuICAgICAgICAoZWwpID0+IHtcbiAgICAgICAgICAgIGVsLnZhbHVlID0gcmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JCYWNrR3JvdW5kRGFyayk7XG4gICAgICAgICAgICBlbC5vbmlucHV0ID0gKHsgdGFyZ2V0IH0pID0+IHtcbiAgICAgICAgICAgICAgICBsZXQgY29sb3IgPSBoZXhUb1JnYigodGFyZ2V0IGFzIEhUTUxJbnB1dEVsZW1lbnQpLnZhbHVlKTtcblxuICAgICAgICAgICAgICAgIGlmICghY29sb3IpIHJldHVybjtcbiAgICAgICAgICAgICAgICB0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckJhY2tHcm91bmREYXJrID0gYCR7Y29sb3Iucn0sICR7Y29sb3IuZ30sICR7Y29sb3IuYn1gO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKVxuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgICAgIH07XG4gICAgICAgIH1cbiAgICApO1xuICB9XG4gIFxuICBhZGRTZXR0aW5nQWN0aXZlQ2FyZENvbG9yTGlnaHQoKTogdm9pZCB7XG4gICAgbmV3IFNldHRpbmcodGhpcy5jb250YWluZXJFbClcbiAgICAgIC5zZXROYW1lKFwiQWN0aXZlIENhcmQgQ29sb3JcIilcbiAgICAgIC5jb250cm9sRWwuY3JlYXRlRWwoXG4gICAgICAgIFwiaW5wdXRcIixcbiAgICAgICAge1xuICAgICAgICAgICAgdHlwZTogXCJjb2xvclwiLFxuICAgICAgICAgICAgdmFsdWU6IHJnYlRvSGV4KHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yQWN0aXZlQ2FyZExpZ2h0KVxuICAgICAgICB9LFxuICAgICAgICAoZWwpID0+IHtcbiAgICAgICAgICAgIGVsLnZhbHVlID0gcmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JBY3RpdmVDYXJkTGlnaHQpO1xuICAgICAgICAgICAgZWwub25pbnB1dCA9ICh7IHRhcmdldCB9KSA9PiB7XG4gICAgICAgICAgICAgICAgbGV0IGNvbG9yID0gaGV4VG9SZ2IoKHRhcmdldCBhcyBIVE1MSW5wdXRFbGVtZW50KS52YWx1ZSk7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWNvbG9yKSByZXR1cm47XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JBY3RpdmVDYXJkTGlnaHQgPSBgJHtjb2xvci5yfSwgJHtjb2xvci5nfSwgJHtjb2xvci5ifWA7XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JOb25BY3RpdmVDYXJkTGlnaHQgPSBgJHtjb2xvci5yIC0gdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JEaWZmQmV0d2VlbkFjdGl2ZX0sICR7Y29sb3IuZyAtIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yRGlmZkJldHdlZW5BY3RpdmV9LCAke2NvbG9yLmIgLSB0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckRpZmZCZXR3ZWVuQWN0aXZlfWA7ICAgICAgICAgICAgICAgIFxuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKVxuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgICAgIH07XG4gICAgICAgIH1cbiAgICApO1xuICB9XG4gIFxuICBhZGRTZXR0aW5nQWN0aXZlQ2FyZENvbG9yRGFyaygpOiB2b2lkIHtcbiAgICBuZXcgU2V0dGluZyh0aGlzLmNvbnRhaW5lckVsKVxuICAgICAgLnNldE5hbWUoXCJBY3RpdmUgQ2FyZCBDb2xvclwiKVxuICAgICAgLmNvbnRyb2xFbC5jcmVhdGVFbChcbiAgICAgICAgXCJpbnB1dFwiLFxuICAgICAgICB7XG4gICAgICAgICAgICB0eXBlOiBcImNvbG9yXCIsXG4gICAgICAgICAgICB2YWx1ZTogcmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JBY3RpdmVDYXJkRGFyaylcbiAgICAgICAgfSxcbiAgICAgICAgKGVsKSA9PiB7XG4gICAgICAgICAgICBlbC52YWx1ZSA9IHJnYlRvSGV4KHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yQWN0aXZlQ2FyZERhcmspO1xuICAgICAgICAgICAgZWwub25pbnB1dCA9ICh7IHRhcmdldCB9KSA9PiB7XG4gICAgICAgICAgICAgICAgbGV0IGNvbG9yID0gaGV4VG9SZ2IoKHRhcmdldCBhcyBIVE1MSW5wdXRFbGVtZW50KS52YWx1ZSk7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWNvbG9yKSByZXR1cm47XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JBY3RpdmVDYXJkRGFyayA9IGAke2NvbG9yLnJ9LCAke2NvbG9yLmd9LCAke2NvbG9yLmJ9YDtcbiAgICAgICAgICAgICAgICB0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvck5vbkFjdGl2ZUNhcmREYXJrID0gYCR7Y29sb3IuciAtIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yRGlmZkJldHdlZW5BY3RpdmV9LCAke2NvbG9yLmcgLSB0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckRpZmZCZXR3ZWVuQWN0aXZlfSwgJHtjb2xvci5iIC0gdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JEaWZmQmV0d2VlbkFjdGl2ZX1gO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKVxuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgICAgIH07XG4gICAgICAgIH1cbiAgICApO1xuICB9XG4gIFxuICBhZGRTZXR0aW5nQ2FyZFRpdGxlRWRnZUNvbG9yRGFyaygpOiB2b2lkIHtcbiAgICBuZXcgU2V0dGluZyh0aGlzLmNvbnRhaW5lckVsKVxuICAgICAgLnNldE5hbWUoXCJUaXRsZSBDYXJkIEVkZ2UgQ29sb3JcIilcbiAgICAgIC5jb250cm9sRWwuY3JlYXRlRWwoXG4gICAgICAgIFwiaW5wdXRcIixcbiAgICAgICAge1xuICAgICAgICAgICAgdHlwZTogXCJjb2xvclwiLFxuICAgICAgICAgICAgdmFsdWU6IHJnYlRvSGV4KHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkRWRnZURhcmspXG4gICAgICAgIH0sXG4gICAgICAgIChlbCkgPT4ge1xuICAgICAgICAgICAgZWwudmFsdWUgPSByZ2JUb0hleCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvclRpdGxlQ2FyZEVkZ2VEYXJrKTtcbiAgICAgICAgICAgIGVsLm9uaW5wdXQgPSAoeyB0YXJnZXQgfSkgPT4ge1xuICAgICAgICAgICAgICAgIGxldCBjb2xvciA9IGhleFRvUmdiKCh0YXJnZXQgYXMgSFRNTElucHV0RWxlbWVudCkudmFsdWUpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFjb2xvcikgcmV0dXJuO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkRWRnZURhcmsgPSBgJHtjb2xvci5yfSwgJHtjb2xvci5nfSwgJHtjb2xvci5ifWA7XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpXG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4ucmVmcmVzaCgpO1xuICAgICAgICAgICAgfTtcbiAgICAgICAgfVxuICAgICk7XG4gIH1cblxuICAgIGFkZFNldHRpbmdDYXJkVGl0bGVFZGdlQ29sb3JMaWdodCgpOiB2b2lkIHtcbiAgICBuZXcgU2V0dGluZyh0aGlzLmNvbnRhaW5lckVsKVxuICAgICAgLnNldE5hbWUoXCJUaXRsZSBDYXJkIEVkZ2UgQ29sb3JcIilcbiAgICAgIC5jb250cm9sRWwuY3JlYXRlRWwoXG4gICAgICAgIFwiaW5wdXRcIixcbiAgICAgICAge1xuICAgICAgICAgICAgdHlwZTogXCJjb2xvclwiLFxuICAgICAgICAgICAgdmFsdWU6IHJnYlRvSGV4KHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkRWRnZUxpZ2h0KVxuICAgICAgICB9LFxuICAgICAgICAoZWwpID0+IHtcbiAgICAgICAgICAgIGVsLnZhbHVlID0gcmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JUaXRsZUNhcmRFZGdlTGlnaHQpO1xuICAgICAgICAgICAgZWwub25pbnB1dCA9ICh7IHRhcmdldCB9KSA9PiB7XG4gICAgICAgICAgICAgICAgbGV0IGNvbG9yID0gaGV4VG9SZ2IoKHRhcmdldCBhcyBIVE1MSW5wdXRFbGVtZW50KS52YWx1ZSk7XG5cbiAgICAgICAgICAgICAgICBpZiAoIWNvbG9yKSByZXR1cm47XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JUaXRsZUNhcmRFZGdlTGlnaHQgPSBgJHtjb2xvci5yfSwgJHtjb2xvci5nfSwgJHtjb2xvci5ifWA7XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpXG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4ucmVmcmVzaCgpO1xuICAgICAgICAgICAgfTtcbiAgICAgICAgfVxuICAgICk7XG4gIH1cbiAgXG4gIGFkZFNldHRpbmdDYXJkVGl0bGVCYWNrZ3JvdW5kQ29sb3JMaWdodCgpOiB2b2lke1xuICAgIG5ldyBTZXR0aW5nKHRoaXMuY29udGFpbmVyRWwpXG4gICAgICAuc2V0TmFtZShcIlRpdGxlIENhcmQgQmFja2dyb3VuZCBDb2xvclwiKVxuICAgICAgLmNvbnRyb2xFbC5jcmVhdGVFbChcbiAgICAgICAgXCJpbnB1dFwiLFxuICAgICAgICB7XG4gICAgICAgICAgICB0eXBlOiBcImNvbG9yXCIsXG4gICAgICAgICAgICB2YWx1ZTogcmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JUaXRsZUNhcmRCYWNrR3JvdW5kTGlnaHQpXG4gICAgICAgIH0sXG4gICAgICAgIChlbCkgPT4ge1xuICAgICAgICAgICAgZWwudmFsdWUgPSByZ2JUb0hleCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvclRpdGxlQ2FyZEJhY2tHcm91bmRMaWdodCk7XG4gICAgICAgICAgICBlbC5vbmlucHV0ID0gKHsgdGFyZ2V0IH0pID0+IHtcbiAgICAgICAgICAgICAgICBsZXQgY29sb3IgPSBoZXhUb1JnYigodGFyZ2V0IGFzIEhUTUxJbnB1dEVsZW1lbnQpLnZhbHVlKTtcbiAgXG4gICAgICAgICAgICAgICAgaWYgKCFjb2xvcikgcmV0dXJuO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkQmFja0dyb3VuZExpZ2h0ID0gYCR7Y29sb3Iucn0sICR7Y29sb3IuZ30sICR7Y29sb3IuYn1gO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKVxuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgICAgIH07XG4gICAgICAgIH1cbiAgICApO1xuICB9XG4gIFxuICBhZGRTZXR0aW5nQ2FyZFRpdGxlQmFja2dyb3VuZENvbG9yRGFyaygpOiB2b2lkIHtcbiAgICBuZXcgU2V0dGluZyh0aGlzLmNvbnRhaW5lckVsKVxuICAgICAgLnNldE5hbWUoXCJUaXRsZSBDYXJkIEJhY2tncm91bmQgQ29sb3JcIilcbiAgICAgIC5jb250cm9sRWwuY3JlYXRlRWwoXG4gICAgICAgIFwiaW5wdXRcIixcbiAgICAgICAge1xuICAgICAgICAgICAgdHlwZTogXCJjb2xvclwiLFxuICAgICAgICAgICAgdmFsdWU6IHJnYlRvSGV4KHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkQmFja0dyb3VuZERhcmspXG4gICAgICAgIH0sXG4gICAgICAgIChlbCkgPT4ge1xuICAgICAgICAgICAgZWwudmFsdWUgPSByZ2JUb0hleCh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvclRpdGxlQ2FyZEJhY2tHcm91bmREYXJrKTtcbiAgICAgICAgICAgIGVsLm9uaW5wdXQgPSAoeyB0YXJnZXQgfSkgPT4ge1xuICAgICAgICAgICAgICAgIGxldCBjb2xvciA9IGhleFRvUmdiKCh0YXJnZXQgYXMgSFRNTElucHV0RWxlbWVudCkudmFsdWUpO1xuXG4gICAgICAgICAgICAgICAgaWYgKCFjb2xvcikgcmV0dXJuO1xuICAgICAgICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkQmFja0dyb3VuZERhcmsgPSBgJHtjb2xvci5yfSwgJHtjb2xvci5nfSwgJHtjb2xvci5ifWA7XG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpXG4gICAgICAgICAgICAgICAgdGhpcy5wbHVnaW4ucmVmcmVzaCgpO1xuICAgICAgICAgICAgfTtcbiAgICAgICAgfVxuICAgICk7XG4gIH1cbiAgXG4gICAgICAvLyBTTElERVIgc2V0dGluZ1xuICAgICAgLy8gbmV3IFNldHRpbmcoY29udGFpbmVyRWwpXG4gICAgICAvLyAgIC5zZXROYW1lKCdEaWZmIEJldHdlZW4gQWN0aXZlICYgTm9uQWN0aXZlIENhcmRzJylcbiAgICAgIC8vICAgLnNldERlc2MoJ1NwY2lmaXkgQ29sb3IgZGlmZmVyZW5jZSBiZXR3ZWVuIGFjdGl2ZSAmIG5vbiBhY3RpdmUgY2FyZHMuIFNldCAwIHRvIGRpYWJsZSBcIkF0dGVudGlvbiBwYW5lXCInKVxuICAgICAgLy8gICAuYWRkU2xpZGVyKHNsaWRlciA9PiBzbGlkZXJcbiAgICAgIC8vICAgICAuc2V0TGltaXRzKDEwMCwgMTAwLCA1KVxuICAgICAgLy8gICAgIC5zZXRWYWx1ZSh0aGlzLnBsdWdpbi5zZXR0aW5ncy5jb2xvckRpZmZCZXR3ZWVuQWN0aXZlKVxuICAgICAgLy8gICAgIC5vbkNoYW5nZSgodmFsdWUpID0+IHtcbiAgICAgIC8vICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yRGlmZkJldHdlZW5BY3RpdmUgPSB2YWx1ZTtcbiAgICAgIC8vICAgICAgIGxldCBhY3RpdmVDb2xvckxpZ2h0ID0gaGV4VG9SZ2IocmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JBY3RpdmVDYXJkRGFyaykpO1xuICAgICAgLy8gICAgICAgbGV0IGFjdGl2ZUNvbG9yRGFyayA9IGhleFRvUmdiKHJnYlRvSGV4KHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yQWN0aXZlQ2FyZERhcmspKTtcbiAgICAgIC8vICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yTm9uQWN0aXZlQ2FyZExpZ2h0ID0gYCR7YWN0aXZlQ29sb3JMaWdodC5yIC0gdmFsdWV9LCAke2FjdGl2ZUNvbG9yTGlnaHQuZyAtIHZhbHVlfSwgJHthY3RpdmVDb2xvckxpZ2h0LmIgLSB2YWx1ZX1gO1xuICAgICAgLy8gICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JOb25BY3RpdmVDYXJkRGFyayA9IGAke2FjdGl2ZUNvbG9yRGFyay5yIC0gdmFsdWV9LCAke2FjdGl2ZUNvbG9yRGFyay5nIC0gdmFsdWV9LCAke2FjdGl2ZUNvbG9yRGFyay5iIC0gdmFsdWV9YDtcbiAgICAgIC8vICAgICAgIHRoaXMucGx1Z2luLnNhdmVEYXRhKHRoaXMucGx1Z2luLnNldHRpbmdzKTtcbiAgICAgIC8vICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgIC8vICAgICB9KVxuICAgICAgLy8gKTtcblxuICBhZGRTZXR0aW5nRGlmZkJldEFjdGl2ZU5vbmFjdGl2ZSgpOiB2b2lkIHtcbiAgICBuZXcgU2V0dGluZyh0aGlzLmNvbnRhaW5lckVsKVxuICAgICAgLnNldE5hbWUoJ0RpZmYgQmV0d2VlbiBBY3RpdmUgJiBOb25BY3RpdmUgQ2FyZHMnKVxuICAgICAgLnNldERlc2MoJ1NldCBDb2xvciBkaWZmZXJlbmNlIGJldHdlZW4gYWN0aXZlICYgbm9uIGFjdGl2ZSBjYXJkcy4gU2V0IHRoaXMgdmFsdWUgMCB0byBkaWFibGUgXCJBdHRlbnRpb24gcGFuZVwiLiBWYWx1ZSByYW5nZTogXCItMjU1fjI1NVwiLicpXG4gICAgICAuYWRkVGV4dCh0ZXh0ID0+IHRleHQuc2V0UGxhY2Vob2xkZXIoJ0RlZmF1bHQ6IDAnKVxuICAgICAgICAuc2V0VmFsdWUoKHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yRGlmZkJldHdlZW5BY3RpdmUgfHwgJycpICsgJycpXG4gICAgICAgIC5vbkNoYW5nZSgodmFsdWUpID0+IHtcbiAgICAgICAgICBsZXQgbnUgPSBOdW1iZXIodmFsdWUpXG4gICAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JEaWZmQmV0d2VlbkFjdGl2ZSA9IG51O1xuICAgICAgICAgIGxldCBhY3RpdmVDb2xvckxpZ2h0ID0gaGV4VG9SZ2IocmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JBY3RpdmVDYXJkTGlnaHQpKTtcbiAgICAgICAgICBsZXQgYWN0aXZlQ29sb3JEYXJrID0gaGV4VG9SZ2IocmdiVG9IZXgodGhpcy5wbHVnaW4uc2V0dGluZ3MuY29sb3JBY3RpdmVDYXJkRGFyaykpO1xuICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yTm9uQWN0aXZlQ2FyZExpZ2h0ID0gYCR7YWN0aXZlQ29sb3JMaWdodC5yIC0gbnV9LCAke2FjdGl2ZUNvbG9yTGlnaHQuZyAtIG51fSwgJHthY3RpdmVDb2xvckxpZ2h0LmIgLSBudX1gO1xuICAgICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmNvbG9yTm9uQWN0aXZlQ2FyZERhcmsgPSBgJHthY3RpdmVDb2xvckRhcmsuciAtIG51fSwgJHthY3RpdmVDb2xvckRhcmsuZyAtIG51fSwgJHthY3RpdmVDb2xvckRhcmsuYiAtIG51fWA7XG4gICAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpO1xuICAgICAgICAgIHRoaXMucGx1Z2luLnJlZnJlc2goKTtcbiAgICAgICAgfSlcbiAgICAgICk7XG4gIH1cblxufVxuXG5leHBvcnQgY2xhc3MgQ2FyZFZpZXdNb2RlQ29tbWFuZHMge1xuICBwbHVnaW46IENhcmRWaWV3TW9kZVBsdWdpbjtcbiAgY29uc3RydWN0b3IocGx1Z2luOiBDYXJkVmlld01vZGVQbHVnaW4pIHtcbiAgICB0aGlzLnBsdWdpbiA9IHBsdWdpbjtcbiAgfVxuXG4gIGFkZFRvZ2dsZVNldHRpbmdDb21tYW5kKGlkOnN0cmluZywgbmFtZTpzdHJpbmcsIHNldHRpbmdOYW1lOnN0cmluZykge1xuICAgIHRoaXMucGx1Z2luLmFkZENvbW1hbmQoe1xuICAgICAgaWQ6IGlkLFxuICAgICAgbmFtZTogbmFtZSxcbiAgICAgIGNhbGxiYWNrOiAoKSA9PiB7XG4gICAgICAgIC8vIHN3aXRjaCB0aGUgc2V0dGluZywgc2F2ZSBhbmQgcmVmcmVzaFxuICAgICAgICAvL0B0cy1pZ25vcmVcbiAgICAgICAgdGhpcy5wbHVnaW4uc2V0dGluZ3Nbc2V0dGluZ05hbWVdID0gIXRoaXMucGx1Z2luLnNldHRpbmdzW3NldHRpbmdOYW1lXTtcbiAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpO1xuICAgICAgICB0aGlzLnBsdWdpbi5yZWZyZXNoKCk7XG4gICAgICB9XG4gICAgfSk7XG4gIH1cblxuICBhZGRDb21tYW5kcygpOiB2b2lkIHtcbiAgICB0aGlzLnBsdWdpbi5hZGRDb21tYW5kKHtcbiAgICAgIGlkOiAndG9nZ2xlLWNhcmQtdmlldy1tb2RlJyxcbiAgICAgIG5hbWU6ICdUb2dnbGUgQ2FyZCBWaWV3JyxcbiAgICAgIGNhbGxiYWNrOiAoKSA9PiB7XG4gICAgICAgIC8vIHN3aXRjaCB0aGUgZGlzYWJsZWQgc2V0dGluZyBhbmQgc2F2ZVxuICAgICAgICB0aGlzLnBsdWdpbi5zZXR0aW5ncy5kaXNhYmxlZCA9ICF0aGlzLnBsdWdpbi5zZXR0aW5ncy5kaXNhYmxlZDtcbiAgICAgICAgdGhpcy5wbHVnaW4uc2F2ZURhdGEodGhpcy5wbHVnaW4uc2V0dGluZ3MpO1xuXG4gICAgICAgIHRoaXMucGx1Z2luLnNldHRpbmdzLmRpc2FibGVkID8gdGhpcy5wbHVnaW4uZGlzYWJsZSgpIDogdGhpcy5wbHVnaW4uZW5hYmxlKCk7XG4gICAgICB9XG4gICAgfSk7XG5cbiAgICB0aGlzLmFkZFRvZ2dsZVNldHRpbmdDb21tYW5kKCd0b2dnbGUtY2FyZC10aXRsZScsICdUb2dnbGUgVGl0bGUgQ2FyZCcsICdjYXJkVGl0bGUnKTtcbiAgfVxufVxuXG5cbmZ1bmN0aW9uIGhleFRvUmdiKGhleDogc3RyaW5nKSB7XG4gICAgbGV0IHJlc3VsdCA9IC9eIz8oW2EtZlxcZF17Mn0pKFthLWZcXGRdezJ9KShbYS1mXFxkXXsyfSkkL2kuZXhlYyhoZXgpO1xuXG4gICAgcmV0dXJuIHJlc3VsdFxuICAgICAgICA/IHtcbiAgICAgICAgICAgICAgcjogcGFyc2VJbnQocmVzdWx0WzFdLCAxNiksXG4gICAgICAgICAgICAgIGc6IHBhcnNlSW50KHJlc3VsdFsyXSwgMTYpLFxuICAgICAgICAgICAgICBiOiBwYXJzZUludChyZXN1bHRbM10sIDE2KVxuICAgICAgICAgIH1cbiAgICAgICAgOiBudWxsO1xufVxuZnVuY3Rpb24gY29tcG9uZW50VG9IZXgoYzogbnVtYmVyKSB7XG4gICAgdmFyIGhleCA9IGMudG9TdHJpbmcoMTYpO1xuICAgIHJldHVybiBoZXgubGVuZ3RoID09IDEgPyBcIjBcIiArIGhleCA6IGhleDtcbn1cbmZ1bmN0aW9uIHJnYlRvSGV4KHJnYjogc3RyaW5nKSB7XG4gICAgbGV0IHJlc3VsdCA9IC9eKFxcZCspLFxccz8oXFxkKyksXFxzPyhcXGQrKS9pLmV4ZWMocmdiKTtcbiAgICBpZiAoIXJlc3VsdCB8fCAhcmVzdWx0Lmxlbmd0aCkge1xuICAgICAgICByZXR1cm4gXCJcIjtcbiAgICB9XG4gICAgcmV0dXJuIGAjJHtjb21wb25lbnRUb0hleChOdW1iZXIocmVzdWx0WzFdKSl9JHtjb21wb25lbnRUb0hleChcbiAgICAgICAgTnVtYmVyKHJlc3VsdFsyXSlcbiAgICApfSR7Y29tcG9uZW50VG9IZXgoTnVtYmVyKHJlc3VsdFszXSkpfWA7XG59XG4iLCJpbXBvcnQgJy4vc3R5bGVzLnNjc3MnXG5pbXBvcnQgeyBQbHVnaW4gfSBmcm9tICdvYnNpZGlhbic7XG5pbXBvcnQgeyBcbiAgQ2FyZFZpZXdNb2RlU2V0dGluZ3MsXG4gIERFRkFVTFRfU0VUVElOR1MsIFxuICBDYXJkVmlld01vZGVTZXR0aW5nVGFiLCBcbiAgQ2FyZFZpZXdNb2RlQ29tbWFuZHMgXG59IGZyb20gJy4vc2V0dGluZ3MnO1xuXG5cbmV4cG9ydCBkZWZhdWx0IGNsYXNzIENhcmRWaWV3TW9kZVBsdWdpbiBleHRlbmRzIFBsdWdpbiB7XG4gIHNldHRpbmdzOiBDYXJkVmlld01vZGVTZXR0aW5ncztcbiAgc3R5bGU6IEhUTUxTdHlsZUVsZW1lbnQgPSBkb2N1bWVudC5oZWFkLmNyZWF0ZUVsKCdzdHlsZScpO1xuXG4gIGFzeW5jIG9ubG9hZCgpIHtcbiAgICAgIGF3YWl0IHRoaXMubG9hZFNldHRpbmdzKCk7XG4gICAgICBpZiAoIXRoaXMuc2V0dGluZ3MuZGlzYWJsZWQpIHRoaXMuZW5hYmxlKCk7XG4gICAgICB0aGlzLmFkZFNldHRpbmdUYWIobmV3IENhcmRWaWV3TW9kZVNldHRpbmdUYWIodGhpcy5hcHAsIHRoaXMpKTtcbiAgICAgIG5ldyBDYXJkVmlld01vZGVDb21tYW5kcyh0aGlzKS5hZGRDb21tYW5kcygpO1xuICB9XG5cbiAgb251bmxvYWQoKXtcbiAgICB0aGlzLmRpc2FibGUoKTtcbiAgfSAgIFxuXG4gIGVuYWJsZSA9ICgpID0+IHtcbiAgICB0aGlzLmFwcC53b3Jrc3BhY2Uub25MYXlvdXRSZWFkeSh0aGlzLnJlYWxseUVuYWJsZSk7XG4gIH1cblxuICByZWFsbHlFbmFibGUgPSAoKSA9PiB7XG4gICAgdGhpcy5hZGRTdHlsZSgpO1xuICB9XG5cbiAgZGlzYWJsZSA9ICgpID0+IHtcbiAgICB0aGlzLnJlbW92ZVN0eWxlKCk7IFxuICB9XG5cbiAgYXN5bmMgbG9hZFNldHRpbmdzKCkge1xuICAgIHRoaXMuc2V0dGluZ3MgPSBPYmplY3QuYXNzaWduKERFRkFVTFRfU0VUVElOR1MsIGF3YWl0IHRoaXMubG9hZERhdGEoKSk7XG4gIH1cblxuICByZWZyZXNoID0gKCkgPT4ge1xuICAgIHRoaXMudXBkYXRlU3R5bGUoKVxuICB9XG5cbiAgcmVtb3ZlU3R5bGUgPSAoKSA9PiB7XG4gICAgY29uc3QgZWwgPSB0aGlzLnN0eWxlO1xuICAgIGlmIChlbCkgZWwucmVtb3ZlKCk7XG4gICAgZG9jdW1lbnQuYm9keS5jbGFzc0xpc3QucmVtb3ZlKCdwbHVnaW4tY2FyZC12aWV3LW1vZGUnKTtcbiAgICBkb2N1bWVudC5ib2R5LmNsYXNzTGlzdC5yZW1vdmUoJ3BsdWdpbi1jYXJkLXZpZXctbW9kZS1jYXJkdGl0bGUnKTtcbiAgICBkb2N1bWVudC5ib2R5LmNsYXNzTGlzdC5yZW1vdmUoJ3BsdWdpbi1jYXJkLXZpZXctbW9kZS1kcm9wc2hhZG93Jyk7XG4gICAgZG9jdW1lbnQuYm9keS5jbGFzc0xpc3QucmVtb3ZlKCdwbHVnaW4tY2FyZC12aWV3LW1vZGUtcmVtb3ZlLXBhbmUtYm91ZGFyaWVzJyk7XG4gICAgZG9jdW1lbnQuYm9keS5jbGFzc0xpc3QucmVtb3ZlKCdwbHVnaW4tY2FyZC12aWV3LW1vZGUtaGlkZS1zY3JvbGxiYXInKTtcbiAgfVxuXG4gIGFkZFN0eWxlID0gKCkgPT4ge1xuICAgIHRoaXMuc3R5bGUuc2V0QXR0cmlidXRlKCd0eXBlJywgJ3RleHQvY3NzJyk7XG5cbiAgICAvLyBhZGQgc3R5bGUgaW4gaGVhZCB0YWdcbiAgICBkb2N1bWVudC5nZXRFbGVtZW50c0J5VGFnTmFtZShcImhlYWRcIilbMF0uYXBwZW5kQ2hpbGQodGhpcy5zdHlsZSk7XG4gICAgZG9jdW1lbnQuYm9keS5jbGFzc0xpc3QuYWRkKCdwbHVnaW4tY2FyZC12aWV3LW1vZGUnKTtcbiAgICB0aGlzLnVwZGF0ZVN0eWxlKCk7XG4gIH1cbiAgXG4gIHVwZGF0ZVN0eWxlID0gKCkgPT4ge1xuICAgIC8vIGEuIHVwZGF0ZSBib29sZWFuIHNldHRpbmdzXG4gICAgZG9jdW1lbnQuYm9keS5jbGFzc0xpc3QudG9nZ2xlKCdwbHVnaW4tY2FyZC12aWV3LW1vZGUtY2FyZHRpdGxlJywgdGhpcy5zZXR0aW5ncy5jYXJkVGl0bGUpO1xuICAgIGRvY3VtZW50LmJvZHkuY2xhc3NMaXN0LnRvZ2dsZSgncGx1Z2luLWNhcmQtdmlldy1tb2RlLWRyb3BzaGFkb3cnLCB0aGlzLnNldHRpbmdzLmNhcmREcm9wU2hhZG93KTtcbiAgICBkb2N1bWVudC5ib2R5LmNsYXNzTGlzdC50b2dnbGUoJ3BsdWdpbi1jYXJkLXZpZXctbW9kZS1yZW1vdmUtcGFuZS1ib3VkYXJpZXMnLCB0aGlzLnNldHRpbmdzLnJlbW92ZVBhbmVCb3VuZGFyaWVzKTtcbiAgICBkb2N1bWVudC5ib2R5LmNsYXNzTGlzdC50b2dnbGUoJ3BsdWdpbi1jYXJkLXZpZXctbW9kZS1oaWRlLXNjcm9sbGJhcicsIHRoaXMuc2V0dGluZ3MuaGlkZVNjcm9sbEJhcik7XG5cbiAgICAvLyBiLiB1cGRhdGUgY3VzdG9tIGNzcyBwcm9wZXJ0aWVzXG4gICAgY29uc3QgZWwgPSB0aGlzLnN0eWxlO1xuICAgIGlmICghZWwpIHRocm93IFwicGx1Z2luLWNhcmQtdmlldy1tb2RlIGVsZW1lbnQgbm90IGZvdW5kIVwiO1xuICAgIGVsc2Uge1xuICAgICAgICBlbC50ZXh0Q29udGVudCA9IGBcbiAgICAgICAgYm9keS5wbHVnaW4tY2FyZC12aWV3LW1vZGUge1xuICAgICAgICAgIC0tY2FyZHZpZXctY2FyZC1ib2FyZGVyLXJhZGl1czogJHt0aGlzLnNldHRpbmdzLmNhcmRDb3JuZXJSYWRpdXN9cHg7XG4gICAgICAgICAgLS1jYXJkdmlldy1lbWJlZGRlZC10aXRsZS1ib2FyZGVyLXJhZGl1czogJHt0aGlzLnNldHRpbmdzLmNhcmRUaXRsZUNvcm5lclJhZGl1c31weDtcbiAgICAgICAgfVxuICAgICAgICBib2R5LnBsdWdpbi1jYXJkLXZpZXctbW9kZS50aGVtZS1saWdodHtcbiAgICAgICAgICAtLWNhcmR2aWV3LWNhcmQtY29sb3ItYWN0aXZlOiByZ2IoJHt0aGlzLnNldHRpbmdzLmNvbG9yQWN0aXZlQ2FyZExpZ2h0fSk7XG4gICAgICAgICAgLS1jYXJkdmlldy1jYXJkLWNvbG9yLW5vbi1hY3RpdmU6IHJnYigke3RoaXMuc2V0dGluZ3MuY29sb3JOb25BY3RpdmVDYXJkTGlnaHR9KTtcbiAgICAgICAgICAtLWNhcmR2aWV3LWJhY2tncm91bmQtY29sb3ItZGVmYXVsdDogcmdiKCR7dGhpcy5zZXR0aW5ncy5jb2xvckJhY2tHcm91bmRMaWdodH0pO1xuICAgICAgICAgIC0taGVhZGVyLWNvbG9yLWJhY2tncm91bmQ6IHJnYigke3RoaXMuc2V0dGluZ3MuY29sb3JUaXRsZUNhcmRCYWNrR3JvdW5kTGlnaHR9KTtcbiAgICAgICAgICAtLWNhcmR2aWV3LWJvcmRlci1jb2xvcjogcmdiKCR7dGhpcy5zZXR0aW5ncy5jb2xvckNhcmRCb3JkZXJMaWdodH0pO1xuICAgICAgICAgIC0tY2FyZHZpZXctZW1iZWRkZWQtdGl0bGUtYm9yZGVyLXJpZ2h0LWNvbG9yLWVkaXQ6IHJnYigke3RoaXMuc2V0dGluZ3MuY29sb3JUaXRsZUNhcmRFZGdlTGlnaHR9KTtcbiAgICAgICAgICAtLWNhcmR2aWV3LWVtYmVkZGVkLXRpdGxlLWJvcmRlci1yaWdodC1jb2xvci1wcmV2aWV3OiByZ2IoJHt0aGlzLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkRWRnZUxpZ2h0fSk7XG4gICAgICAgIH1cbiAgICAgICAgYm9keS5wbHVnaW4tY2FyZC12aWV3LW1vZGUudGhlbWUtZGFya3tcbiAgICAgICAgICAtLWNhcmR2aWV3LWNhcmQtY29sb3ItYWN0aXZlOiByZ2IoJHt0aGlzLnNldHRpbmdzLmNvbG9yQWN0aXZlQ2FyZERhcmt9KTtcbiAgICAgICAgICAtLWNhcmR2aWV3LWNhcmQtY29sb3Itbm9uLWFjdGl2ZTogcmdiKCR7dGhpcy5zZXR0aW5ncy5jb2xvck5vbkFjdGl2ZUNhcmREYXJrfSk7XG4gICAgICAgICAgLS1jYXJkdmlldy1iYWNrZ3JvdW5kLWNvbG9yLWRlZmF1bHQ6IHJnYigke3RoaXMuc2V0dGluZ3MuY29sb3JCYWNrR3JvdW5kRGFya30pO1xuICAgICAgICAgIC0taGVhZGVyLWNvbG9yLWJhY2tncm91bmQ6IHJnYigke3RoaXMuc2V0dGluZ3MuY29sb3JUaXRsZUNhcmRCYWNrR3JvdW5kRGFya30pO1xuICAgICAgICAgIC0tY2FyZHZpZXctYm9yZGVyLWNvbG9yOiByZ2IoJHt0aGlzLnNldHRpbmdzLmNvbG9yQ2FyZEJvcmRlckRhcmt9KTtcbiAgICAgICAgICAtLWNhcmR2aWV3LWVtYmVkZGVkLXRpdGxlLWJvcmRlci1yaWdodC1jb2xvci1lZGl0OiByZ2IoJHt0aGlzLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkRWRnZURhcmt9KTtcbiAgICAgICAgICAtLWNhcmR2aWV3LWVtYmVkZGVkLXRpdGxlLWJvcmRlci1yaWdodC1jb2xvci1wcmV2aWV3OiByZ2IoJHt0aGlzLnNldHRpbmdzLmNvbG9yVGl0bGVDYXJkRWRnZURhcmt9KTtcbiAgICAgICAgfVxuICAgICAgICBgO1xuICAgIH1cbiAgfVxuICBcbn1cblxuIl0sIm5hbWVzIjpbIlNldHRpbmciLCJQbHVnaW5TZXR0aW5nVGFiIiwiUGx1Z2luIl0sIm1hcHBpbmdzIjoiOzs7O0FBQUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxJQUFJLGFBQWEsR0FBRyxTQUFTLENBQUMsRUFBRSxDQUFDLEVBQUU7QUFDbkMsSUFBSSxhQUFhLEdBQUcsTUFBTSxDQUFDLGNBQWM7QUFDekMsU0FBUyxFQUFFLFNBQVMsRUFBRSxFQUFFLEVBQUUsWUFBWSxLQUFLLElBQUksVUFBVSxDQUFDLEVBQUUsQ0FBQyxFQUFFLEVBQUUsQ0FBQyxDQUFDLFNBQVMsR0FBRyxDQUFDLENBQUMsRUFBRSxDQUFDO0FBQ3BGLFFBQVEsVUFBVSxDQUFDLEVBQUUsQ0FBQyxFQUFFLEVBQUUsS0FBSyxJQUFJLENBQUMsSUFBSSxDQUFDLEVBQUUsSUFBSSxNQUFNLENBQUMsU0FBUyxDQUFDLGNBQWMsQ0FBQyxJQUFJLENBQUMsQ0FBQyxFQUFFLENBQUMsQ0FBQyxFQUFFLENBQUMsQ0FBQyxDQUFDLENBQUMsR0FBRyxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUMsRUFBRSxDQUFDO0FBQzFHLElBQUksT0FBTyxhQUFhLENBQUMsQ0FBQyxFQUFFLENBQUMsQ0FBQyxDQUFDO0FBQy9CLENBQUMsQ0FBQztBQUNGO0FBQ08sU0FBUyxTQUFTLENBQUMsQ0FBQyxFQUFFLENBQUMsRUFBRTtBQUNoQyxJQUFJLElBQUksT0FBTyxDQUFDLEtBQUssVUFBVSxJQUFJLENBQUMsS0FBSyxJQUFJO0FBQzdDLFFBQVEsTUFBTSxJQUFJLFNBQVMsQ0FBQyxzQkFBc0IsR0FBRyxNQUFNLENBQUMsQ0FBQyxDQUFDLEdBQUcsK0JBQStCLENBQUMsQ0FBQztBQUNsRyxJQUFJLGFBQWEsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxDQUFDLENBQUM7QUFDeEIsSUFBSSxTQUFTLEVBQUUsR0FBRyxFQUFFLElBQUksQ0FBQyxXQUFXLEdBQUcsQ0FBQyxDQUFDLEVBQUU7QUFDM0MsSUFBSSxDQUFDLENBQUMsU0FBUyxHQUFHLENBQUMsS0FBSyxJQUFJLEdBQUcsTUFBTSxDQUFDLE1BQU0sQ0FBQyxDQUFDLENBQUMsSUFBSSxFQUFFLENBQUMsU0FBUyxHQUFHLENBQUMsQ0FBQyxTQUFTLEVBQUUsSUFBSSxFQUFFLEVBQUUsQ0FBQyxDQUFDO0FBQ3pGLENBQUM7QUF1Q0Q7QUFDTyxTQUFTLFNBQVMsQ0FBQyxPQUFPLEVBQUUsVUFBVSxFQUFFLENBQUMsRUFBRSxTQUFTLEVBQUU7QUFDN0QsSUFBSSxTQUFTLEtBQUssQ0FBQyxLQUFLLEVBQUUsRUFBRSxPQUFPLEtBQUssWUFBWSxDQUFDLEdBQUcsS0FBSyxHQUFHLElBQUksQ0FBQyxDQUFDLFVBQVUsT0FBTyxFQUFFLEVBQUUsT0FBTyxDQUFDLEtBQUssQ0FBQyxDQUFDLEVBQUUsQ0FBQyxDQUFDLEVBQUU7QUFDaEgsSUFBSSxPQUFPLEtBQUssQ0FBQyxLQUFLLENBQUMsR0FBRyxPQUFPLENBQUMsRUFBRSxVQUFVLE9BQU8sRUFBRSxNQUFNLEVBQUU7QUFDL0QsUUFBUSxTQUFTLFNBQVMsQ0FBQyxLQUFLLEVBQUUsRUFBRSxJQUFJLEVBQUUsSUFBSSxDQUFDLFNBQVMsQ0FBQyxJQUFJLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsT0FBTyxDQUFDLEVBQUUsRUFBRSxNQUFNLENBQUMsQ0FBQyxDQUFDLENBQUMsRUFBRSxFQUFFO0FBQ25HLFFBQVEsU0FBUyxRQUFRLENBQUMsS0FBSyxFQUFFLEVBQUUsSUFBSSxFQUFFLElBQUksQ0FBQyxTQUFTLENBQUMsT0FBTyxDQUFDLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxFQUFFLENBQUMsT0FBTyxDQUFDLEVBQUUsRUFBRSxNQUFNLENBQUMsQ0FBQyxDQUFDLENBQUMsRUFBRSxFQUFFO0FBQ3RHLFFBQVEsU0FBUyxJQUFJLENBQUMsTUFBTSxFQUFFLEVBQUUsTUFBTSxDQUFDLElBQUksR0FBRyxPQUFPLENBQUMsTUFBTSxDQUFDLEtBQUssQ0FBQyxHQUFHLEtBQUssQ0FBQyxNQUFNLENBQUMsS0FBSyxDQUFDLENBQUMsSUFBSSxDQUFDLFNBQVMsRUFBRSxRQUFRLENBQUMsQ0FBQyxFQUFFO0FBQ3RILFFBQVEsSUFBSSxDQUFDLENBQUMsU0FBUyxHQUFHLFNBQVMsQ0FBQyxLQUFLLENBQUMsT0FBTyxFQUFFLFVBQVUsSUFBSSxFQUFFLENBQUMsRUFBRSxJQUFJLEVBQUUsQ0FBQyxDQUFDO0FBQzlFLEtBQUssQ0FBQyxDQUFDO0FBQ1AsQ0FBQztBQUNEO0FBQ08sU0FBUyxXQUFXLENBQUMsT0FBTyxFQUFFLElBQUksRUFBRTtBQUMzQyxJQUFJLElBQUksQ0FBQyxHQUFHLEVBQUUsS0FBSyxFQUFFLENBQUMsRUFBRSxJQUFJLEVBQUUsV0FBVyxFQUFFLElBQUksQ0FBQyxDQUFDLENBQUMsQ0FBQyxHQUFHLENBQUMsRUFBRSxNQUFNLENBQUMsQ0FBQyxDQUFDLENBQUMsQ0FBQyxDQUFDLE9BQU8sQ0FBQyxDQUFDLENBQUMsQ0FBQyxDQUFDLEVBQUUsRUFBRSxJQUFJLEVBQUUsRUFBRSxFQUFFLEdBQUcsRUFBRSxFQUFFLEVBQUUsRUFBRSxDQUFDLEVBQUUsQ0FBQyxFQUFFLENBQUMsRUFBRSxDQUFDLENBQUM7QUFDckgsSUFBSSxPQUFPLENBQUMsR0FBRyxFQUFFLElBQUksRUFBRSxJQUFJLENBQUMsQ0FBQyxDQUFDLEVBQUUsT0FBTyxFQUFFLElBQUksQ0FBQyxDQUFDLENBQUMsRUFBRSxRQUFRLEVBQUUsSUFBSSxDQUFDLENBQUMsQ0FBQyxFQUFFLEVBQUUsT0FBTyxNQUFNLEtBQUssVUFBVSxLQUFLLENBQUMsQ0FBQyxNQUFNLENBQUMsUUFBUSxDQUFDLEdBQUcsV0FBVyxFQUFFLE9BQU8sSUFBSSxDQUFDLEVBQUUsQ0FBQyxFQUFFLENBQUMsQ0FBQztBQUM3SixJQUFJLFNBQVMsSUFBSSxDQUFDLENBQUMsRUFBRSxFQUFFLE9BQU8sVUFBVSxDQUFDLEVBQUUsRUFBRSxPQUFPLElBQUksQ0FBQyxDQUFDLENBQUMsRUFBRSxDQUFDLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxFQUFFO0FBQ3RFLElBQUksU0FBUyxJQUFJLENBQUMsRUFBRSxFQUFFO0FBQ3RCLFFBQVEsSUFBSSxDQUFDLEVBQUUsTUFBTSxJQUFJLFNBQVMsQ0FBQyxpQ0FBaUMsQ0FBQyxDQUFDO0FBQ3RFLFFBQVEsT0FBTyxDQUFDLEVBQUUsSUFBSTtBQUN0QixZQUFZLElBQUksQ0FBQyxHQUFHLENBQUMsRUFBRSxDQUFDLEtBQUssQ0FBQyxHQUFHLEVBQUUsQ0FBQyxDQUFDLENBQUMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxDQUFDLFFBQVEsQ0FBQyxHQUFHLEVBQUUsQ0FBQyxDQUFDLENBQUMsR0FBRyxDQUFDLENBQUMsT0FBTyxDQUFDLEtBQUssQ0FBQyxDQUFDLEdBQUcsQ0FBQyxDQUFDLFFBQVEsQ0FBQyxLQUFLLENBQUMsQ0FBQyxJQUFJLENBQUMsQ0FBQyxDQUFDLEVBQUUsQ0FBQyxDQUFDLEdBQUcsQ0FBQyxDQUFDLElBQUksQ0FBQyxJQUFJLENBQUMsQ0FBQyxDQUFDLEdBQUcsQ0FBQyxDQUFDLElBQUksQ0FBQyxDQUFDLEVBQUUsRUFBRSxDQUFDLENBQUMsQ0FBQyxDQUFDLEVBQUUsSUFBSSxFQUFFLE9BQU8sQ0FBQyxDQUFDO0FBQ3pLLFlBQVksSUFBSSxDQUFDLEdBQUcsQ0FBQyxFQUFFLENBQUMsRUFBRSxFQUFFLEdBQUcsQ0FBQyxFQUFF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
