import { PixelRatio, StyleSheet, Text, View } from "react-native";
import React, { useState } from "react";

import { Dropdown } from "react-native-element-dropdown";
import colors from "../resources/colors/colors";

const CustomDropdownReport = ({ data, labelId, onChange }) => {
  const [value, setValue] = useState(null);
  const [isFocus, setIsFocus] = useState(false);
  return (
    <Dropdown
      style={[styles.dropdown, isFocus && { borderColor: "blue" }]}
      placeholderStyle={styles.placeholderStyle}
      selectedTextStyle={styles.selectedTextStyle}
      containerStyle={styles.container}
      itemTextStyle={{ margin: PixelRatio.roundToNearestPixel(-8) }}
      iconStyle={styles.iconStyle}
      data={data}
      maxHeight={200}
      labelField="shift_name"
      valueField="shift_id"
      placeholder={!isFocus ? "Select item" : "Select Shift"}
      value={labelId}
      onFocus={() => setIsFocus(true)}
      onBlur={() => setIsFocus(false)}
      onChange={item => {
        // setValue(item.value);
        onChange(item.shift_id);
        setIsFocus(false);
      }}
      
    />
  );
};

export default CustomDropdownReport;

const styles = StyleSheet.create({
  container: {
    backgroundColor: "white",
    width: PixelRatio.roundToNearestPixel(130),
    alignSelf: "center",
    borderRadius: PixelRatio.roundToNearestPixel(15),
  },
  dropdown: {
    backgroundColor: "white",
    width: "95%",
    height: PixelRatio.roundToNearestPixel(50),
    borderColor: colors.black,
    borderWidth: 1,
    borderRadius: PixelRatio.roundToNearestPixel(20),
    paddingHorizontal: 2,
    marginHorizontal: 2,
  },
  icon: {
    marginRight: 5,
  },
  label: {
    position: "absolute",
    backgroundColor: "white",
    right: 22,
    top: 8,
    zIndex: 999,
    paddingHorizontal: 8,
    fontSize: 14,
  },
  placeholderStyle: {
    fontSize: 16,
    color: colors.black,
    marginLeft: PixelRatio.roundToNearestPixel(5),
  },
  selectedTextStyle: {
    width:"100%",
    fontSize: 16,
    fontWeight: "100%",
    color: colors.black,
    marginLeft: PixelRatio.roundToNearestPixel(8),
  },
  iconStyle: {
    width: 20,
    height: 20,
  },
  inputSearchStyle: {
    height: 40,
    fontSize: 16,
  },
});
