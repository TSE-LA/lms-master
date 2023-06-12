import {PublishedCourse} from "../../../../core/src/lib/dashboard/dashboard-model/dashboard-model";
import {DropdownModel} from "../dropdown/dropdownModel";

export interface DashletModel {
  id: string,
  name: string,
  loading: boolean
}

export interface LearnerSuccessInput {
  selectedYear: string,
  selectedGroup: string,
  selectedHalfYear: string
}

export const CHART_DATA: PublishedCourse[] = [
  {categoryName: 'Ерөнхий ур чадвар', categoryId: '123', count: 5},
  {categoryName: 'Програмын сургалт', categoryId: '234', count: 11},
  {categoryName: 'Багц', categoryId: '345', count: 0},
  {categoryName: 'Шинэ суралцагч', categoryId: '456', count: 8},
  {categoryName: 'Идэвхжүүлэх', categoryId: '567', count: 21},
  {categoryName: 'Нээлттэй', categoryId: '678', count: 17},
  {categoryName: 'Мэдвэл зохих', categoryId: '789', count: 3},
  {categoryName: 'Интернэт', categoryId: '890', count: 1},
  {categoryName: 'HR зөвлөгөө', categoryId: '901', count: 41},
]
export const PROMO_CHART_DATA: PublishedCourse[] = [
  {categoryName: 'Ерөнхий ур чадвар', categoryId: '1234', count: 15},
  {categoryName: 'Програмын сургалт', categoryId: '2345', count: 11},
  {categoryName: 'Багц', categoryId: '3456', count: 0},
  {categoryName: 'Шинэ суралцагч', categoryId: '4567', count: 8},
  {categoryName: 'Идэвхжүүлэх', categoryId: '5678', count: 11},
  {categoryName: 'Нээлттэй', categoryId: '6789', count: 7},
  {categoryName: 'Мэдвэл зохих', categoryId: '7891', count: 3},
  {categoryName: 'Интернэт', categoryId: '8901', count: 21},
  {categoryName: 'HR зөвлөгөө', categoryId: '9012', count: 31},
]

export const DASHLET_DATA: DashletModel = {
  id: 'PUBLISHED_COURSE',
  name: 'НИЙТЭЛСЭН ЦАХИМ СУРГАЛТ',
  loading: false
};


export const LEARNER_SUCCESS_PERCENTAGES = [100, 80, 60, 40, 20, 0];

export const SEMESTERS: DropdownModel[] = [
  {id: 'FIRST_HALF' , name: '1-р хагас жил', action: 'first_half'},
  {id: 'SECOND_HALF' , name: '2-р хагас жил', action: 'second_half'}
]

export const LEARNER_SUCCESS_GROUPS: DropdownModel[] = [
  {id: 'MY_GROUP', name: 'Миний групп'},
  {id: 'ALL_GROUP', name: 'Бүх групп'}
]
